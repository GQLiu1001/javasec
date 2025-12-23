package javase.re008;

public class Main {
    public static void main(String[] args) {
        // region static 
        Student.name = "李四";
        // 此时被加载进 方法区（JDK 8 及以上为元空间） 但是对象没有被创建
        // 静态变量随着类加载而被加载，比对象先出现
        // 此后所有 Student 的 name 均为 "李四"
        // 被 static 修饰的成员变量 -> 静态变量
        //      被 该类 所有对象共享！
        // 如果加 final 则需要在类加载时被初始化/静态代码块 （需要规范ALLCAP）
        // public static final String NAME = "李四"; 

        // 对于静态方法 基本都是测试类以及工具类中
        // 直接类名调用即可
        // 静态方法中，只能访问静态
        // 非静态方法可以访问所有
        // 静态方法没有 this 关键字
        CalUtil.cal(1, 3);
        // 静态成员（变量 / 方法）属于类本身，不依赖对象实例，只要类被加载就能使用；
        // 实例成员（变量 / 方法）属于对象实例，必须创建对象后才能通过对象访问。
        // endregion
    }
}


class Student{
    public Student(int age) {
        this.age = age;
    }
    static String name;
    private int age;
    public static String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public static void setName(String name) {
        Student.name = name;
    }
    public void setAge(int age) {
        this.age = age;
    }

}

class CalUtil{

    public static void cal(int num1, int num2){
        System.out.println("输入了" + num1 + " " + num2);
    }
}