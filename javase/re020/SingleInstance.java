package javase.re020;

public class SingleInstance {
    public static void main(String[] args) {
        // 饿汉式
        Singleton01 instance1 = Singleton01.getInstance();
        Singleton01 instance2 = Singleton01.getInstance();

        if (instance1 == instance2) {
            System.out.println("Both instances are the same.");
        } else {
            System.out.println("Instances are different.");
        }
        // 懒汉式
        // 正确的Lambda写法：直接用 () -> {代码块} 赋值给Runnable变量
        Runnable r1 = () -> {
            Singleton02 instance = Singleton02.getInstance();
            System.out.println(Thread.currentThread().getName() + " : " + instance);
        };
        Runnable r2 = () -> {
            Singleton02 instance = Singleton02.getInstance();
            System.out.println(Thread.currentThread().getName() + " : " + instance);
        };
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();
    }
}