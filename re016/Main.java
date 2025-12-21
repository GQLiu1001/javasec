package re016;

import java.util.List;

public class Main {
    // 在 main 方法中，如果你想调用一个方法，它必须满足以下两个条件之一：
    // 它是 static 的（静态方法）。
    // 它是通过 new 出来的对象（实例方法）。
    public static void main(String[] args) {
        // region 泛型
        int a = 10;
        sout(a);
    }

    // 最好就是 <T>
    static <T> void sout(T a) {
        System.out.println(a);
    }

    // extends 允许 T 及其子类 (更具体的类型)
    // PECS: Producer Extends (如果是用来生产/取出数据的，用 extends)
    // 禁止写入 遍历
    // super 允许 T 及其父类 (更宽泛的类型)
    // PECS: Consumer Super (如果是用来消费/存入数据的，用 super)
    // 可以写入 添加
    <T extends Cat> void checkCat(T cat) {
        System.out.println(cat);
    }

    // super 就是这个T类需要是LihuaCat的父类
    // 它允许你传入一个 List<Cat> 或者 List<Animal> 给这个方法
    // 通配符 ? 主要用于引用变量或作为参数类型
    void checkCatList(List<? super LihuaCat> catList) {
        // 根据 PECS 原则（Producer Extends, Consumer Super）
        // ? super T 是“消费者”，适合写入（Add），不适合读取（Get）。
        // for (Cat cat : catList) {
        // System.out.println(cat);
        // }
        catList.add(new LihuaCat("小狸"));
    }

}

class Animal {
}

class Cat extends Animal {
}

class Dog extends Animal {
}

class LihuaCat extends Cat {
    String name;

    public LihuaCat(String nameString) {
        this.name = name;
    }
}

class BandianDog extends Dog {
}