package re005;

public class Main {
    public static void main(String[] args) {
        Student a = new Student(3, "good");
        // a.age = 3; // 这就是因为没有 private 修饰 可以随意赋值导致的
        System.out.println(a.toString());
        // System.out.println(a.name);// 这就是因为没有 private 修饰 可以随意赋值导致的
        System.out.println(a.getName());
        // No enclosing instance of type Student is accessible.
        // Must qualify the allocation with an enclosing instance of type Student (e.g.
        // x.new A() where x is an instance of Student)
        // 静态内部类 直接通过外部类名创建：外部类.内部类()(无需先创建student实例)
        // Student.Bag bag = new Student.Bag("语文") ;
        // 非静态内部类 必须通过外部类实例创建：外部实例.new 内部类()
        Student.Bag bag = a.new Bag("语文");
        // System.out.println(bag.getBook());
        a.setBag(bag);
        System.out.println(a.getAge());
        System.out.println(a.getName());
        System.out.println(a.getBag().getBook());

        // ===================== 静态内部类 =====================
        // 1. 静态内部类：无需 Student 实例，直接创建
        Student.StaticBag staticBag = new Student.StaticBag("英语");
        Student b = new Student(3, "good");
        b.setStaticBag(staticBag);
        System.out.println("静态内部类的书：" + b.getStaticBag().getBook());

        // ===================== 非静态内部类 =====================
        // 1. 先有 Student 实例 a，再创建非静态内部类
        Student.NonStaticBag nonStaticBag = b.new NonStaticBag("数学");
        b.setNonStaticBag(nonStaticBag);
        System.out.println("非静态内部类的书：" + b.getNonStaticBag().getBook());
    }

}

// region 面向对象
// 必须先设计类 才能获取到对象
// static 修饰类的语法仅适用于「内部类」
// Java 语法明确禁止给顶级类加 static

class Student {
    // 成员变量
    private int age;
    private String name;
    private Bag bag;
    private StaticBag staticBag; // 静态内部类引用
    private NonStaticBag nonStaticBag; // 非静态内部类引用
    // 构造器

    public Student(int age, String name, Bag bag) {
        // this代表当前对象，谁调用，this指的就是谁
        this.age = age;
        // 左边的 name 是成员变量，右边的 name 是参数
        this.name = name;
        this.bag = bag;
    }

    public Student(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Student() {
    }

    // 成员方法
    // getter/setter
    public StaticBag getStaticBag() {
        return staticBag;
    }

    public void setStaticBag(StaticBag staticBag) {
        this.staticBag = staticBag;
    }

    public NonStaticBag getNonStaticBag() {
        return nonStaticBag;
    }

    public void setNonStaticBag(NonStaticBag nonStaticBag) {
        this.nonStaticBag = nonStaticBag;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    // static class Bag {
    class Bag {
        private String book;

        public Bag(String book) {
            this.book = book;
        }

        public String getBook() {
            return book;
        }

        public void setBook(String book) {
            this.book = book;
        }

    }

    // 静态内部类
    // static 修饰类的语法仅适用于「内部类」
    static class StaticBag {
        String book;

        public StaticBag(String book) {
            this.book = book;
        }

        public String getBook() {
            return book;
        }
    }

    // 非静态内部类
    class NonStaticBag {
        String book;

        public NonStaticBag(String book) {
            this.book = book;
        }

        public String getBook() {
            return book;
        }
    }

}
    // endregion

    // region this
    // 代表的是当前使用对象的地址
class Ob{
    int age;
    // 在 static 方法中，你根本不需要对象就可以调用它（比如直接用 Ob.fun()）。
    // 此时，根本没有“当前对象”，this 指向的是一片虚无。
    // Java 编译器为了防止这种逻辑混乱，强制规定“静态方法中不能使用 this”。

    // static void fun(){
    void fun(){
        int age = 10;
        System.out.println(age); // 10
        System.out.println(this.age); // ob.fun() 的ob的age 初始化为0
    }
}
class Fu{
    void go(){
        Ob ob = new Ob();
        ob.fun();
    }
}

    // endregion