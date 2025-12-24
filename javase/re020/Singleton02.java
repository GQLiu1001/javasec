package javase.re020;

public class Singleton02 {
    // 懒汉式 在需要的时候才创建对象
    // 单例 第一步 ：隐藏构造器 只能由本类创建对象
    private Singleton02() {
    }

    // 单例 第二步 ：定义一个静态成员变量用于存储一个对象
    // 懒汉单例 要在调用的时候才创建！
    private static Singleton02 instance;

    // 单例 第三步 ：提供一个公共的静态方法用于获取该类的唯一实例
    // public static Singleton02 getInstance() {
    // if (instance == null) {
    // return instance = new Singleton02();
    // } else {
    // return instance;
    // }
    // }
    // 加锁的懒汉式
    public static synchronized Singleton02 getInstance() {
        if (instance == null) {
            instance = new Singleton02();
        }
        return instance;
    }
    // 另一种加锁的懒汉式：DCL 双重检查锁定
    // 需要 volatile 关键字修饰 instance 变量 
    // volatile 关键字确保 instance 变量的可见性和禁止指令重排序
    // private static volatile Singleton02 instance;
    /* public static Singleton02 getInstance() {
        if (instance == null) {
            synchronized (Singleton02.class) {
                if (instance == null) {
                    instance = new Singleton02();
                }
            }
        }
        return instance;
    } */
}
