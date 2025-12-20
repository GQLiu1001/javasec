package re009;

public class Main {
    public static void main(String[] args) {
        // region 继承
        // java 为 单继承设计 支持多层继承
        // 增加代码复用性
        // 子类 extends 父类
        // 子类还可以在父类基础上增加其他功能
        // LihuaCat lihua = new Animal(); // 父类是不完全的

        // 继承 可以继承成员变量 不能继承构造器 以及非私有的成员方法
        // 对于方法 每个类都会有一个虚方法表 记录所有上级父类的 非static 非final 非private 的方法
        // 例子： HashiqiDog (8虚+destory) Dog(7虚+gurd) Animal(5虚+eat+drink)
        // Object(1,2,3,4,5)

        // 注意 就近原则
        // Fu{String hobby = "fu"}
        // Zi{String hobby = "zi"}
        // sout(hobby) // 就近
        // sout(this.hobby) // 本类
        // sout(super.hobby) // 当前类的直接父类（最近的父类）

        // 方法的重写 即 虚方法表中的虚方法覆盖

        // 子类初始化前会先调用父类构造方法
        // 父类中的属性都是共性的内容，而且都是可以被子类继承下来的，
        // 而父类中的空参构造就是给父类中的属性一个默认初始化，
        // 如果没有这一步，子类在使用父类中的属性的时候就没有了值了
        // 子类构造方法第一行是默认 隐藏的 super();
        // 如想用父类有参构造 super(num1,num2);
        Animal a = new BuouCat();
        a.drink();
        ;
        a.eat();
        LihuaCat lihua = new LihuaCat();
        lihua.catchMice();
        lihua.drink();
        lihua.eat();
        TaidiDog taidi = new TaidiDog();
        taidi.drink();
        taidi.eat();
        taidi.gurd();
        taidi.close();
        HashiqiDog hashiqi = new HashiqiDog();
        taidi.eat();
        hashiqi.eat();

    }
}

class Animal {
    void eat() {
        System.out.println("eat");
    }

    void drink() {
        System.out.println("drink");
    }
}

class Cat extends Animal {
    void catchMice() {
        System.out.println("抓老鼠");
    }
}

class BuouCat extends Cat {

}

class LihuaCat extends Cat {

}

class Dog extends Animal {
    @Override
    void eat() {
        System.out.println("就要吃狗粮");
    }

    void gurd() {
        System.out.println("看家");
    }
}

class HashiqiDog extends Dog {
    @Override
    void eat() {
        super.eat(); // HashiqiDog的eat为父级的eat
    }

    void destory() {
        System.out.println(this.getClass() + "拆家");
    }
}

class TaidiDog extends Dog {
    @Override
    void eat() {
        System.out.println("就要吃TaidiDog的食物");
    }

    void close() {
        System.out.println(this.getClass().getName() + "蹭一蹭");
    }
}

// endregion