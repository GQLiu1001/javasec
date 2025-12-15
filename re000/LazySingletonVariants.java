package re000;
public class LazySingletonVariants {

    public static void main(String[] args) {
        // ===== synchronized 方法版 =====
        LazySyncMethod s1 = LazySyncMethod.getInstance();
        LazySyncMethod s2 = LazySyncMethod.getInstance();
        System.out.println("SyncMethod same? " + (s1 == s2)); // true

        // ===== DCL 版 =====
        LazyDCL d1 = LazyDCL.getInstance();
        LazyDCL d2 = LazyDCL.getInstance();
        System.out.println("DCL same? " + (d1 == d2)); // true
    }

    /**
     * 懒汉式：synchronized 整个 getInstance()
     * 含义：第一次调用时创建；以后每次调用都要进入同步锁（开销更大）。
     */
    static class LazySyncMethod {
        private static LazySyncMethod instance;

        private LazySyncMethod() {}

        public static synchronized LazySyncMethod getInstance() {
            if (instance == null) {
                instance = new LazySyncMethod();
            }
            return instance;
        }
    }

    /**
     * 懒汉式：Double-Checked Locking (DCL) + volatile
     * 含义：大多数时候不加锁，只有第一次初始化时加锁；volatile 保证可见性和禁止指令重排。
     */
    static class LazyDCL {
        // 关键：volatile
        private static volatile LazyDCL instance;

        private LazyDCL() {}

        public static LazyDCL getInstance() {
            // 第一次检查：不加锁，快路径
            if (instance == null) {
                synchronized (LazyDCL.class) {
                    // 第二次检查：防止多个线程同时通过第一次检查后重复创建
                    if (instance == null) {
                        instance = new LazyDCL();
                    }
                }
            }
            return instance;
        }
    }
}
