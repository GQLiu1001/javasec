package re000;

public class Threads {
    // 共享锁对象
    private static final Object LOCK = new Object();
    // 控制打印标志：true时打印A，false时打印B
    private static boolean flag = true;
    // 打印次数
    private static final int TIMES = 10;

    public static void main(String[] args) {
        // 线程A：打印"A"
        Thread threadA = new Thread(() -> {
            for (int i = 0; i < TIMES; ) {
                synchronized (LOCK) {
                    // 若flag为false，说明该打印B，线程A等待
                    while (!flag) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    // 打印A并修改标志
                    System.out.print("A");
                    flag = false;
                    i++; // 仅成功打印后计数
                    // 唤醒线程B
                    LOCK.notify();
                }
            }
        }, "Thread-A");

        // 线程B：打印"B"
        Thread threadB = new Thread(() -> {
            for (int i = 0; i < TIMES; ) {
                synchronized (LOCK) {
                    // 若flag为true，说明该打印A，线程B等待
                    while (flag) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    // 打印B并修改标志
                    System.out.print("B");
                    flag = true;
                    i++; // 仅成功打印后计数
                    // 唤醒线程A
                    LOCK.notify();
                }
            }
        }, "Thread-B");

        threadA.start();
        threadB.start();
    }
}
