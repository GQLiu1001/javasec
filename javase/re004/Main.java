package javase.re004;

public class Main {
    public static void main(String[] args) {
        // region 方法
        // 方法是程序中的最小执行单元
        func();
        // Cannot make a static reference to the non-static method func() from the type Main
        // 静态 属于类 类加载时就存在不需要创建对象直接使用
        // 非静态 属于对象 必须new对象之后才存在
        // static方法 调用 非static方法 相当于 
        //      还没有“生出”一个具体的人，就想直接使用这个人的“心脏跳动”功能
    }   

    // 方法的重载 与返回值无关 JVM根据 * 输入参数 * 自动调用
    static void func(){
        System.out.println(1);
    }
    static int func(int a){  
        System.out.println(a);
        return 1;
    }
    static String func(String a){
        System.out.println(a);
        return "hello";
    }

    // endregion

    // region 方法内存
    // 如前 main -> 所需方法进栈 ... 
    // 如果出现基本数据类型则在当前方法栈中开辟 
    // 如果出现引用数据类型：数组、字符串、类。则栈中只有局部变量(值为堆内存地址) 在堆中开辟对象
    // 注意传值，引用数据类型是传地址值 而基本数据类型是值
    //
    // 方法代码在方法区，对象实体在堆，变量在栈
    // 现代 Java 字符串常量在堆里
    //
    // endregion
}
