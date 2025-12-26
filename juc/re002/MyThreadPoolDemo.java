package juc.re002;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

/**
 * 手写一个“可面试讲原理”的线程池（简化版）
 *
 * 你能用它回答 ThreadPoolExecutor 的核心构成：
 * - Worker（线程 + 任务执行循环）
 * - workers 集合（HashSet）+ mainLock 保护
 * - workQueue（阻塞队列）存放等待任务
 * - core/max/keepAliveTime 控制线程数量与回收
 * - RejectedExecutionHandler 拒绝策略
 *
 * ⚠️ 这是教学/面试级实现，不是生产级（没有实现所有边界和统计字段、没有ctl位运算状态机）
 */
public class MyThreadPoolDemo {

    public static void main(String[] args) throws Exception {
        MyThreadPool pool = new MyThreadPool(
                2,                 // corePoolSize
                4,                 // maximumPoolSize
                2, TimeUnit.SECONDS, // keepAliveTime：非核心线程空闲多久退出
                new MyBlockingQueue<>(3), // 有界阻塞队列：容量=3
                new NamedThreadFactory("order-worker"),
                new CallerRunsPolicy() // 拒绝策略：调用者执行（不丢）
        );

        // 提交一些任务，观察：先建core线程 -> 入队 -> 队列满再扩到max -> 最后拒绝策略
        for (int i = 1; i <= 12; i++) {
            final int id = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " handling task-" + id);
                sleepSilently(600);
            });
        }

        // 等一会儿再关闭
        Thread.sleep(5000);
        pool.shutdown();

        // shutdown 后继续提交任务会被拒绝（根据策略）
        try {
            pool.execute(() -> System.out.println("should be rejected"));
        } catch (Exception e) {
            System.out.println("after shutdown submit -> " + e);
        }
    }

    private static void sleepSilently(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    // ============================================================
    // 线程池实现
    // ============================================================

    /**
     * 手写线程池核心类
     */
    public static class MyThreadPool {

        // -------------------- 核心参数 --------------------
        private final int corePoolSize;             // 核心线程数（常驻）
        private final int maximumPoolSize;          // 最大线程数（峰值）
        private final long keepAliveNanos;          // 非核心线程空闲存活时间（纳秒）
        private final MyBlockingQueue<Runnable> workQueue; // 等待队列（阻塞队列）
        private final ThreadFactory threadFactory;  // 线程工厂（命名、守护线程等）
        private final RejectedExecutionHandler handler; // 拒绝策略

        // -------------------- worker 管理 --------------------
        /**
         * 存放所有worker（对应 ThreadPoolExecutor 里的 HashSet<Worker> workers）
         * 为什么是HashSet：增删快、无顺序要求
         */
        private final HashSet<Worker> workers = new HashSet<>();

        /**
         * mainLock：保护workers集合、线程池关闭状态、worker增删的并发安全
         * （对应 ThreadPoolExecutor 的 mainLock）
         */
        private final ReentrantLock mainLock = new ReentrantLock();

        /**
         * 条件变量：用于等待“线程池彻底终止”
         * 这里只做演示（不实现 awaitTermination 的完整语义也可以）
         */
        private final Condition termination = mainLock.newCondition();

        // -------------------- 状态管理（简化版） --------------------
        private volatile boolean isShutdown = false;

        public MyThreadPool(int corePoolSize,
                            int maximumPoolSize,
                            long keepAliveTime,
                            TimeUnit unit,
                            MyBlockingQueue<Runnable> workQueue,
                            ThreadFactory threadFactory,
                            RejectedExecutionHandler handler) {
            if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize) {
                throw new IllegalArgumentException("illegal pool size");
            }
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveNanos = unit.toNanos(keepAliveTime);
            this.workQueue = Objects.requireNonNull(workQueue);
            this.threadFactory = Objects.requireNonNull(threadFactory);
            this.handler = Objects.requireNonNull(handler);
        }

        // ============================================================
        // execute：提交任务（面试核心：新任务进来会发生什么）
        // ============================================================

        /**
         * 提交一个任务（Runnable）
         *
         * 关键编排顺序（与 ThreadPoolExecutor 的典型逻辑一致）：
         * 1) workerCount < core -> 新建核心worker直接执行
         * 2) 否则 -> 入队
         * 3) 入队失败（队列满）且 workerCount < max -> 新建非核心worker执行
         * 4) 仍不行 -> 拒绝策略
         */
        public void execute(Runnable command) {
            Objects.requireNonNull(command);

            // shutdown 后不再接收新任务（ThreadPoolExecutor 在不同状态下有更细分逻辑）
            if (isShutdown) {
                reject(command);
                return;
            }

            // ---- 1) 先尝试创建核心worker（直接执行，不入队）----
            if (tryAddWorker(command, true)) {
                return;
            }

            // ---- 2) 尝试入队（线程数已到core或创建core失败）----
            // 入队成功：任务交给worker循环从队列取走执行
            if (workQueue.offer(command)) {
                // 入队后仍可能出现：池被shutdown（这里简化处理）
                if (isShutdown && workQueue.remove(command)) {
                    reject(command);
                } else {
                    // 确保至少有一个worker在跑（可能core为0或worker刚退出）
                    ensureAtLeastOneWorker();
                }
                return;
            }

            // ---- 3) 队列满：尝试扩容到maximumPoolSize（创建非核心worker）----
            if (tryAddWorker(command, false)) {
                return;
            }

            // ---- 4) 队列满 + 线程已到max：拒绝 ----
            reject(command);
        }

        /**
         * shutdown：不再接收新任务，但会把队列里的任务跑完
         */
        public void shutdown() {
            mainLock.lock();
            try {
                isShutdown = true;
                // 只中断“空闲”的worker（这里简化：直接中断全部，让它们尽快从take/poll返回）
                for (Worker w : workers) {
                    w.interruptIfStarted();
                }
            } finally {
                mainLock.unlock();
            }
        }

        /**
         * 立即关闭：尝试打断正在执行的任务，并清空队列（教学用）
         */
        public List<Runnable> shutdownNow() {
            mainLock.lock();
            try {
                isShutdown = true;
                for (Worker w : workers) {
                    w.interruptIfStarted();
                }
                return workQueue.drainAll();
            } finally {
                mainLock.unlock();
            }
        }

        // ============================================================
        // worker 管理
        // ============================================================

        /**
         * 尝试新增 worker
         *
         * @param firstTask 新worker启动后第一个要执行的任务（可为null）
         * @param core      true=按corePoolSize限制创建；false=按maximumPoolSize限制创建
         */
        private boolean tryAddWorker(Runnable firstTask, boolean core) {
            mainLock.lock();
            try {
                if (isShutdown) {
                    return false;
                }

                int wc = workers.size();
                int limit = core ? corePoolSize : maximumPoolSize;

                if (wc >= limit) {
                    return false;
                }

                Worker w = new Worker(firstTask);
                Thread t = threadFactory.newThread(w);
                w.thread = t;

                // workers集合的增删一定要在mainLock保护下
                workers.add(w);

                // 启动线程（真正向JVM要线程）
                t.start();
                return true;

            } finally {
                mainLock.unlock();
            }
        }

        /**
         * 确保至少有一个worker在工作（避免“队列有任务但没有worker”的尴尬）
         * ThreadPoolExecutor 里有类似的 ensurePrestart / addWorker(null,true) 逻辑。
         */
        private void ensureAtLeastOneWorker() {
            mainLock.lock();
            try {
                if (!isShutdown && workers.isEmpty()) {
                    tryAddWorker(null, true);
                }
            } finally {
                mainLock.unlock();
            }
        }

        private void reject(Runnable command) {
            handler.rejectedExecution(command, this);
        }

        // ============================================================
        // Worker：线程池内部“真正干活的人”
        // ============================================================

        /**
         * Worker = “线程 + 执行循环 + 可中断控制”
         *
         * ThreadPoolExecutor 里 Worker 继承 AQS，用来实现：
         * - 运行期间加锁，避免空闲中断误伤（interruptIdleWorkers）
         * - 标记worker状态、辅助shutdown控制
         *
         * 我们这里用更直观的 ReentrantLock 来表达同样的语义：
         * - 执行任务时 lock，表示“我在忙”
         * - 空闲时 unlock，表示“我可被视为idle”
         *
         * 面试讲法：Worker 的锁/AQS用于“控制中断与idle判定”，不是用于任务排队。
         */
        private final class Worker implements Runnable {
            private Runnable firstTask;
            private Thread thread;

            // 用锁标记“正在执行任务”这段临界区（简化表达AQS独占语义）
            private final ReentrantLock runLock = new ReentrantLock();

            Worker(Runnable firstTask) {
                this.firstTask = firstTask;
            }

            @Override
            public void run() {
                try {
                    runWorker(this);
                } finally {
                    // 线程退出：从workers集合移除，并尝试触发终止信号
                    processWorkerExit(this);
                }
            }

            void interruptIfStarted() {
                Thread t = thread;
                if (t != null) {
                    t.interrupt();
                }
            }

            boolean isIdle() {
                // 没拿到锁 => 正在执行任务 => 非空闲
                return !runLock.isLocked();
            }
        }

        /**
         * worker的主循环：
         * - 先执行firstTask（如果有）
         * - 再不断从队列取任务执行
         * - 取不到任务：核心线程可一直等；非核心线程按keepAlive超时退出（这里简化）
         */
        private void runWorker(Worker w) {
            Runnable task = w.firstTask;
            w.firstTask = null;

            while (task != null || (task = getTask()) != null) {
                // 执行任务期间加锁：表达“我正在忙”
                w.runLock.lock();
                try {
                    // shutdownNow 的时候 interrupt 会打断 sleep/take/poll 等阻塞点
                    task.run();
                } catch (RuntimeException ex) {
                    // execute 提交的任务异常会直接抛出到线程的 UncaughtExceptionHandler（这里让它继续抛也行）
                    // 我们选择打印，便于你观察现象
                    System.err.println("task threw exception: " + ex);
                } finally {
                    w.runLock.unlock();
                }

                task = null;
            }
        }

        /**
         * 从队列取任务：
         * - 若当前worker数量 > corePoolSize：允许超时退出（poll）
         * - 否则：核心线程长期等待（take）
         *
         * ThreadPoolExecutor 更复杂：allowCoreThreadTimeOut、timed-out重试、状态机等
         */
        private Runnable getTask() {
            boolean timed = false;

            mainLock.lock();
            int wc;
            try {
                wc = workers.size();
            } finally {
                mainLock.unlock();
            }

            timed = (wc > corePoolSize);

            try {
                if (timed) {
                    // 非核心线程：空闲超过keepAlive就退出（返回null）
                    return workQueue.poll(keepAliveNanos, TimeUnit.NANOSECONDS);
                } else {
                    // 核心线程：一直等任务
                    return workQueue.take();
                }
            } catch (InterruptedException e) {
                // 被shutdownNow/shutdown打断：
                // - 如果已经shutdown且队列空了，让线程退出
                // - 否则继续循环尝试
                if (isShutdown && workQueue.isEmpty()) {
                    return null;
                }
                Thread.currentThread().interrupt();
                return null; // 简化：被中断直接返回null退出
            }
        }

        /**
         * worker退出后的清理：
         * - 从workers集合移除
         * - 如果池shutdown且workers清空且队列清空 -> 认为terminated
         */
        private void processWorkerExit(Worker w) {
            mainLock.lock();
            try {
                workers.remove(w);
                if (isShutdown && workers.isEmpty() && workQueue.isEmpty()) {
                    termination.signalAll();
                } else {
                    // 如果还有队列任务但worker不足，补一个worker（防止都退出）
                    if (!isShutdown && !workQueue.isEmpty() && workers.isEmpty()) {
                        tryAddWorker(null, true);
                    }
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    // ============================================================
    // 简单阻塞队列（有界）
    // ============================================================

    /**
     * 一个最小可用的有界阻塞队列：
     * - offer：不阻塞，满了直接返回false
     * - put：阻塞直到有空间（这里没用到）
     * - take：阻塞直到有元素
     * - poll(timeout)：超时拿不到返回null
     *
     * 用 ReentrantLock + 两个Condition 实现：
     * - notEmpty：队列非空
     * - notFull：队列未满
     */
    public static class MyBlockingQueue<T> {
        private final Deque<T> deque = new ArrayDeque<>();
        private final int capacity;

        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull = lock.newCondition();

        public MyBlockingQueue(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity must > 0");
            this.capacity = capacity;
        }

        public boolean offer(T x) {
            lock.lock();
            try {
                if (deque.size() >= capacity) return false;
                deque.addLast(x);
                notEmpty.signal();
                return true;
            } finally {
                lock.unlock();
            }
        }

        public void put(T x) throws InterruptedException {
            lock.lock();
            try {
                while (deque.size() >= capacity) {
                    notFull.await();
                }
                deque.addLast(x);
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (deque.isEmpty()) {
                    notEmpty.await();
                }
                T v = deque.removeFirst();
                notFull.signal();
                return v;
            } finally {
                lock.unlock();
            }
        }

        public T poll(long timeout, TimeUnit unit) throws InterruptedException {
            long nanos = unit.toNanos(timeout);
            lock.lock();
            try {
                while (deque.isEmpty()) {
                    if (nanos <= 0) return null;
                    nanos = notEmpty.awaitNanos(nanos);
                }
                T v = deque.removeFirst();
                notFull.signal();
                return v;
            } finally {
                lock.unlock();
            }
        }

        public boolean remove(T x) {
            lock.lock();
            try {
                boolean removed = deque.remove(x);
                if (removed) notFull.signal();
                return removed;
            } finally {
                lock.unlock();
            }
        }

        public boolean isEmpty() {
            lock.lock();
            try {
                return deque.isEmpty();
            } finally {
                lock.unlock();
            }
        }

        public List<T> drainAll() {
            lock.lock();
            try {
                List<T> list = new ArrayList<>(deque);
                deque.clear();
                notFull.signalAll();
                return list;
            } finally {
                lock.unlock();
            }
        }
    }

    // ============================================================
    // 线程工厂（命名）
    // ============================================================

    public interface ThreadFactory {
        Thread newThread(Runnable r);
    }

    public static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger idx = new AtomicInteger(1);

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(prefix + "-" + idx.getAndIncrement());
            t.setDaemon(false); // 业务线程不要daemon
            return t;
        }
    }

    // ============================================================
    // 拒绝策略
    // ============================================================

    public interface RejectedExecutionHandler {
        void rejectedExecution(Runnable r, MyThreadPool executor);
    }

    /** 直接抛异常（默认风格） */
    public static class AbortPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, MyThreadPool executor) {
            throw new RejectedExecutionException("Task " + r + " rejected from " + executor);
        }
    }

    /** 调用者线程执行：不丢任务，但会拖慢上游（通常最“安全”） */
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, MyThreadPool executor) {
            if (r != null) {
                r.run();
            }
        }
    }

    /** 静默丢弃（谨慎使用） */
    public static class DiscardPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, MyThreadPool executor) {
            // do nothing
        }
    }

    /** 丢弃队列头部最老任务，再尝试入队（教学版） */
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, MyThreadPool executor) {
            // 这里没有暴露出队列的“丢头再入队”API，演示用：直接丢弃
            // 你要完整实现可以在 MyBlockingQueue 里加 poll/removeFirst。
            // executor.workQueue.poll(0, TimeUnit.NANOSECONDS); executor.execute(r);
            // 为避免递归/死循环，生产实现要非常谨慎。
        }
    }

    public static class RejectedExecutionException extends RuntimeException {
        public RejectedExecutionException(String msg) {
            super(msg);
        }
    }
}
