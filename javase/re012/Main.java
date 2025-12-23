package javase.re012;

public class Main {
    public static void main(String[] args) {
        // region 抽象类
        // 子类强制重写 abstract extends
        // 抽取共性 但无法确定具体方法体 抽象成abstract

        ID id = ID.create(23, "小李");
        Worker one = new Worker(id);
        System.out.println(one.getId().getId());
        one.study();
        Person.good();
    }

}

abstract class Func {
    // 如果有抽象方法 那么类一定也要加abstract
    // 由子类实现
    abstract void work(int a);

    // 可以有具体方法
    void soild() {
        System.out.println("hello");
    }
}

class FuncA extends Func {

    @Override
    void work(int a) {
        System.out.println("我重写了");
    }

}
// endregion

// region 接口
interface Person {
    // 接口中声明的变量，无论是否显式修饰
    // 都会被 Java 编译器自动加上public static final
    
    int a = 10; // 等价于 public static final int a = 10;

    // Java 9及以上支持：private静态变量（仅接口内部静态方法可用）
    // private static int count = 0;

    public void work();

    // public static ID init(int age, String name) {
    //     count++; // 静态方法中使用私有静态变量
    //     int index = age * 3;
    //     int num = name.length() + index;
    //     return new ID(num);
    // }

    // JDK8为应对接口升级 default 关键字
    public default void study(){
        System.out.println("不需要实现类实现");
        talk();
    };
    // JDK9私有方法 非静态是给默认方法服务的
    private void talk(){
        System.out.println("hello talk");
    }
    // 给静态方法服务的
    private static void show(){
        System.out.println("hello show");
    }
    public static void good(){
        // talk(); //Cannot make a static reference to the non-static method talk() from the type PersonJava(603979977)
        show();
    }
}

class ID {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // 私有化构造器，强制通过静态工厂方法创建实例
    private ID(int id) {
        this.id = id;
    }

    // 静态工厂方法放在产品类自身
    public static ID create(int age, String name) {
        int index = age * 3;
        int num = name.length() + index;
        return new ID(num);
    }
}

class Worker implements Person {
    private ID id;

    @Override
    public void work() {
        System.out.println("working");
    }

    // @Override
    // public void study(){
    //     System.out.println("我重写了");
    // }

    public ID getId() {
        return id;
    }

    public Worker(ID id) {
        this.id = id;
    }

    public void setId(ID id) {
        this.id = id;
    }

}
// endregion

// region 理解
// 抽象类是对 “一类事物” 的抽象 （定义了 “是什么”），体现的是继承关系（is-a）
// 可以继承抽象类的属性，方法，重写抽象方法
// 接口是对 “行为 / 能力” 的抽象 （定义了 “能做什么”），体现的是行为契约（can-do）
// 只能实现方法