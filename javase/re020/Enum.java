package javase.re020;

public class Enum {
    // Java 枚举类型是一种特殊的类，用于表示一组常量值
    // Sum 枚举确实是定义在
    // 外部类 Enum 中的静态嵌套枚举（静态内部枚举），属于静态内部类的范畴
    //
    // 枚举主要做的是信息标志和信息分类。
    // 枚举的特殊规则是：
    // 在类中定义的枚举默认被 static 修饰，即使你没有显式写 static。
    // 枚举类默认被 final 修饰，不能被继承。
    // 枚举类继承了 java.lang.Enum 类。注意 java.lang.Enum已经重写了Object类的toString()方法
    // 枚举类可以有构造器、方法和字段，但构造器必须是私有的（private）。
    // 枚举类是多例设计模式。

    // 旧时代用常量定义星期
    // public static final int SUNDAY = 0;
    // public static final int MONDAY = 1;
    // public static final int TUESDAY = 2;
    public enum WeekDay {
        // SUNDAY,
        // MONDAY,
        // TUESDAY,
        // WEDNESDAY,
        // THURSDAY,
        // FRIDAY,
        // SATURDAY
        // 枚举常量（键） + 构造器赋值（值）
        SUNDAY(0, "星期日"),
        MONDAY(1, "星期一"),
        TUESDAY(2, "星期二"),
        WEDNESDAY(3, "星期三"),
        THURSDAY(4, "星期四"),
        FRIDAY(5, "星期五"),
        SATURDAY(6, "星期六");

        // 枚举的自定义属性（值的部分）
        private final int code; // 数字编码
        private final String desc; // 中文描述

        // 私有构造器：初始化自定义属性（必须private，可省略）
        private WeekDay(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // getter方法：获取枚举的“值”
        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public static void main(String[] args) {
        WeekDay today = WeekDay.WEDNESDAY;
        // 枚举类重写了 toString() 方法？
        System.out.println(today);
        switch (today) {
            case MONDAY:
                System.out.println("Today is " + today.getDesc() + "." + "and code is " + today.getCode());
                break;
            case TUESDAY:
                System.out.println("Today is " + today.getDesc() + "." + "and code is " + today.getCode());
                break;
            case WEDNESDAY:
                System.out.println("Today is " + today.getDesc() + "." + "and code is " + today.getCode());
                break;
            case THURSDAY:
                System.out.println("Today is " + today.getDesc() + "." + "and code is " + today.getCode());
                break;
            case FRIDAY:
                System.out.println("Today is " + today.getDesc() + "." + "and code is " + today.getCode());
                break;
            case SATURDAY:
                System.out.println("Today is " + today.getDesc() + "." + "and code is " + today.getCode());
                break;
            case SUNDAY:
                System.out.println("Today is " + today.getDesc() + "." + "and code is " + today.getCode());
                break;
        }

        Student stu = new Student("Alice", 20);
        System.out.println(stu);
    }

    public static class Student {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

    }
}

// class Student {
// private String name;
// private int age;

// public String getName() {
// return name;
// }

// public int getAge() {
// return age;
// }

// public Student(String name, int age) {
// this.name = name;
// this.age = age;
// }

// public void setName(String name) {
// this.name = name;
// }

// public void setAge(int age) {
// this.age = age;
// }

// @Override
// public String toString() {
// return "Student{" +
// "name='" + name + '\'' +
// ", age=" + age +
// '}';
// }

// }