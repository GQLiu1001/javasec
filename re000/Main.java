package re000;
public class Main {
    // class : java代码的基本单元
    // 类的静态成员变量（类级别，所有对象共享，不依赖对象）
    public static int number;
    // 类的私有静态成员变量（类级别，仅当前类可访问）
    // 这一句只做了一件事：声明一个“引用变量”，默认值是 null。
    private static MyInterface myInterface;
    // 需要 “随时变化” 的实例 每次用都 new（或工厂 new）
    // springboot可以@Scope("prototype")，容器每次注入都会new新实例
    // 单例（全局复用） 初始化一次（静态成员 / 单例模式） 利用 springboot IOC 容器管理
    private static Factory factory;
    private static AppleFactory appleFactory;

    public static void main(String[] args) {
        System.out.println("Hello, World!");
        // 静态方法不能直接访问非静态成员
        System.out.println(number);

        // region 数据类型 及 接口的使用
        // java 分为基本数据类型 和 引用数据类型
        // 基本数据类型：8种
        int a = 10; // 整数类型 int long short byte
        double b = 3.14; // 浮点类型 double float
        char c = 'A'; // 字符类型 char
        boolean d = true; // 布尔类型 boolean
        // 引用数据类型：类、接口、数组
        String str = "Hello"; // 类 String
        int[] arr = { 1, 2, 3 }; // 数组
        // 局部变量
        MyInterface impl = new MyInterface() {
            @Override
            public void myMethod() {
                System.out.println("MyInterface method implementation");
            }
        };// 接口实现类的匿名对象
          // 使用Lambda表达式实现接口方法
        MyInterface impl1 = () -> System.out.println("MyInterface method implementation");
        // MyInterface myInterface = null;
        impl.myMethod();// 调用接口方法
        impl1.myMethod();// 调用接口方法
        System.out.println(myInterface);
        myInterface = () -> System.out.println("私有成员变量的接口实现");
        myInterface.myMethod(); // 调用方法
        // endregion

        // region 工厂模式
        // 创建工厂
        // AppleFactory appleFactory = new AppleFactory();

        // MyInterface dep = new MyInterface() {
        // @Override
        // public void myMethod() {
        // System.out.println("工厂依赖的DI逻辑");
        // }
        // };
        // appleFactory = new AppleFactory(dep);
        // new 了一个 MyInterface 的实现对象（用 Lambda/匿名类都行）
        // 把它当参数传给 AppleFactory 的构造器
        // AppleFactory 把它存到 this.myInterface 里，以后 createFruit() 里要用
        appleFactory = new AppleFactory(() -> System.out.println("工厂依赖的DI逻辑"));

        // 工厂创建产品
        Fruit apple = appleFactory.createFruit();
        // 调用产品的方法
        apple.grow();
        // endregion
    }
}

// 只能有一个public类，且类名必须与文件名相同
interface MyInterface {
    void myMethod();

}

interface Factory1 {
    void create();
}

class AppleFactory1 implements Factory1 {
    private MyInterface myInterface;

    @Override
    public void create() {
        myInterface = () -> System.out.println("di");
        myInterface.myMethod();
        System.out.println("Creating an apple");
    }

    public MyInterface getMyInterface() {
        return myInterface;
    }

    public void setMyInterface(MyInterface myInterface) {
        this.myInterface = myInterface;
    }
}

// 第一步：定义产品接口（工厂要创建的对象）
interface Fruit {
    void grow(); // 产品的行为
}

// 第二步：产品实现类
class Apple implements Fruit {

    @Override
    public void grow() {
        System.out.println("Apple is growing");
    }
}

// 第三步：工厂接口（定义“创建产品”的契约）
interface Factory {
    Fruit createFruit(); // 明确：工厂的职责是创建Fruit产品
}

// 第四步：工厂实现类（结合DI）
class AppleFactory implements Factory {
    // 类（class）有构造器：用来在 new 的时候初始化对象
    // 如果一个类没有写任何构造器，Java 编译器会自动给你生成一个无参构造器

    private MyInterface myInterface; // 工厂的依赖

    // DI：外部注入依赖
    // public void setMyInterface(MyInterface myInterface) {
    // this.myInterface = myInterface;
    // }

    // 构造器注入 工厂对象一创建出来，就已经有依赖了
    public AppleFactory(MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    @Override
    public Fruit createFruit() {
        // 先执行依赖的逻辑（DI的体现）
        myInterface.myMethod();
        // 工厂核心：创建产品对象
        return new Apple();
    }

    public MyInterface getMyInterface() {
        return myInterface;
    }

    public void setMyInterface(MyInterface myInterface) {
        this.myInterface = myInterface;
    }
}

// region static
// static 成员（字段/方法）属于 类本身，不需要对象也能用
// 非 static 成员属于 某一个具体对象，必须先有对象才能用
class A {
    int x = 10; // 非静态：属于某个对象
    static int y = 20; // 静态：属于类

    static void f() {
        System.out.println(y); // ✅可以：y 属于类
        // System.out.println(x); // ❌不行：x 属于对象，但这里没对象
    }
}

// 想用 x，你得先有对象：
// static void f() {
// A a = new A();
// System.out.println(a.x); // ✅通过对象访问
// }
// endregion

// region public private
// public 成员：任何类都能访问
// private 成员：只有当前类能访问
class B {
    public int a = 10; // 任何类都能访问
    private int b = 20; // 只有 B 类能访问

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void show() {
        System.out.println(a); // ✅可以访问
        System.out.println(b); // ✅可以访问
    }
}
class C {
    public void display() {
        B bObj = new B();
        System.out.println(bObj.a); // ✅可以访问 public 成员
        // System.out.println(bObj.b); // ❌不行，private 成员不能访问
        System.out.println(bObj.getB()); // ✅通过 public 方法访问 private 成员
    }
}
// endregion