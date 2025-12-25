package javase.re013;

import javase.re013.Car.Engine;

public class Main {
    public static void main(String[] args) {
        // region 内部类
        // 内部类表示外部类的一部分 内部类单独出现没有任何意义
        // 内部类可以访问外部类的成员 包括私有
        // 外部类想要访问内部类成员 需要创建对象
        Car a = new Car();
        // a.showEngine();
        // new 内部类的方法
        Engine e = a.new Engine();
        e.setEngineName("梅塞德斯");
        a.setEngine(e);
        a.showEngine();
        // endregion

        // region 成员内部类
        // 👆
        // endregion

        // region 静态内部类
        // 静态内部类不需要先创建外部
        // 因为是静态 所以内部类只能用外部的 static 成员
        // Outer.Inner oi = new Outer.Inner();
        // oi.非静态方法;
        // Outer.Inner.静态方法;
        // endregion

        // region 局部内部类
        // 写在方法里的
        // endregion

        // region 匿名内部类
        //  
        FuncA abs = new FuncA() {
            void fucn() {
                System.out.println("hello");
            }
        };
        abs.fucn();
        // 可以减少一些对象的创建
        new FuncA() {
            void fucn() {
                System.out.println("hello fucn");
            }
        }.fucn();
        // Lambda 只能简化函数式接口的匿名实现 目标类型是接口 只有一个抽象方法
        Func abs1 = () -> System.out.println("hello lambda");
        abs1.func();
        // endregion

    }
}

interface Func {
    void func();
}

abstract class FuncA {
    abstract void fucn();
}

class Car {
    private String carName;
    private int carMadeYear;
    private Engine engine;

    public Car(String carName, int carMadeYear, Engine engine) {
        this.carName = carName;
        this.carMadeYear = carMadeYear;
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Car() {
    }

    public Car(String carName, int carMadeYear) {
        this.carName = carName;
        this.carMadeYear = carMadeYear;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public int getCarMadeYear() {
        return carMadeYear;
    }

    public void setCarMadeYear(int carMadeYear) {
        this.carMadeYear = carMadeYear;
    }

    public void showEngine() {
        // 外部类想要访问内部类成员 需要创建对象
        System.out.println(engine.getEngineName());
    }

    class Engine {
        private String engineName;

        public Engine() {
        }

        public Engine(String engineName) {
            this.engineName = engineName;
        }

        void showEngine() {
            System.out.println("engineName = " + engineName);
        }

        public String getEngineName() {
            return engineName;
        }

        public void setEngineName(String engineName) {
            this.engineName = engineName;
        }
    }
}
