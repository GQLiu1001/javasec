package javase.re006;

public class Main {
    public static void main(String[] args) {
        // region String
        // 字符串不会改变 只会创建新的
        // 拼接 默认 new
        // 如果 "a" + "b" 完全等同于 "ab" 但是 a + "b" 则是强制 new
        // 堆中常量池 如果不写 new 会看常量池有没有现成的直接引用 如果 new 强制开辟

        // 创建方式
        String a = new String("abc");
        String b = "aaa";
        char[] char1 = { 'a', 'b' };
        String c = new String(char1);
        String d = new String();
        System.out.println(a + " " + b + " " + c);
        System.out.println(d); // ""
        Sd s = new Sd();
        System.out.println(s); // javase.re006.Sd@3af49f1c

        // s1 = new String("abc")：s1记录的是堆里那个新 String 对象的地址。
        // s2 = "abc"：s2记录的是串池里 "abc" 对象的地址。
        // new String("abc")里的"abc"会进串池（如果串池没有的话）
        // 但 new 操作会额外在堆里造个新对象 
        //      ——s1指向的是堆里的新对象，不是串池里的那个
        String s1 = new String("abc");
        String s2 = "abc";
        System.out.println(s1 == s2); // false

        // endregion

        // region == equals
        // == :
        //      基本数据类型 -> 比较数据值
        //      引用数据类型 -> 比较地址值（是否为同一个对象）
        // equals: 比较内容
        // endregion

        // region StringBuilder StringBuffer
        // StringBuffer 线程安全 方法加了synchronized锁 锁开销大
        // StringBuilder 线程不安全
        // append
        // reverse
        // toString
        // endregion

        // region 链式编程
        // 链式编程的核心是方法返回一个能继续调用方法的对象引用
        //   返回当前类的引用（最常见）
        //   返回父类 / 子类类型（协变返回）
        //   返回其他类的引用（跨类链式）只要后续方法属于该对象即可 Stream流
        // endregion
    }
}

class Sd {
    int name;
}