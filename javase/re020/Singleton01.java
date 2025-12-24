package javase.re020;

public class Singleton01 {
    // 饿汉式 在拿对象的时候对象早已经创建好了
    // 单例 第一步 ：隐藏构造器 只能由本类创建对象
    private Singleton01() {}
    // 单例 第二步 ：定义一个静态成员变量用于存储一个对象
    private static Singleton01 instance = new Singleton01();
    // 单例 第三步 ：提供一个公共的静态方法用于获取该类的唯一实例
    public static Singleton01 getInstance() {
        if (instance == null) {
            return instance = new Singleton01();
        }else {
            return instance;
        }
    }

}
