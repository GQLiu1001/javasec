package javase.re014;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // region Runtime
        // 1. public static Runtime getRuntime() —— 获取当前系统的运行环境对象
        Runtime runtime = Runtime.getRuntime();

        // 2. public int availableProcessors() —— 获得CPU的线程数
        int cpuThreadCount = runtime.availableProcessors();
        System.out.println("CPU线程数：" + cpuThreadCount);

        // 3. public long maxMemory() —— JVM能从系统中获取的总内存大小（单位：byte）
        long maxMem = runtime.maxMemory();
        System.out.println("JVM最大可用内存：" + maxMem + " byte");

        // 4. public long totalMemory() —— JVM已经从系统中获取的总内存大小（单位：byte）
        long totalMem = runtime.totalMemory();
        System.out.println("JVM已分配内存：" + totalMem + " byte");

        // 5. public long freeMemory() —— JVM剩余内存大小（单位：byte）
        long freeMem = runtime.freeMemory();
        System.out.println("JVM剩余内存：" + freeMem + " byte");

        // 6. public Process exec(String command) —— 运行cmd命令（示例：Windows打开记事本）
        //
        // Linux 下：直接查找PATH环境变量中的可执行文件（如/bin/ls、/usr/bin/java）
        //
        // Windows 下：会隐式调用cmd.exe /c来解析命令（比如执行notepad.exe时，
        // 实际是cmd.exe /c notepad.exe），这是 Windows 的特殊处理，Linux 无此默认行为。
        try {
            runtime.exec("notepad.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 7. public void exit(int status) —— 停止虚拟机（参数0=正常退出，非0=异常退出）
        // （注：此处注释避免程序提前结束，实际使用时按需调用）
        // runtime.exit(0);
        // endregion

        // region Object
        // clone 对象拷贝 对象复制
        // 1.需要重写 Object 的 clone 方法
        // 2.让 javabean 实现 Cloneable 接口
        // 3.创建原对象 并 clone
        // User u1 = new User(11, "null");
        // User u2 = (User) u1.clone();
        // System.out.println(u1.getAge() + u1.getName());
        // System.out.println(u2.getAge() + u2.getName());
        Address addr = new Address("北京");
        User user1 = new User(20,"张三", addr);
        User user2 = user1.clone();
        
        // 修改user2的Address，不再影响user1
        user2.getAddress().setCity("上海");
        System.out.println(user1.getAddress().getCity()); // 输出北京（深拷贝生效）
        System.out.println(user2.getAddress().getCity()); // 输出上海
    }
}

class User implements Cloneable {
    private int age;
    private String name;
    private Address address;

    public User(int age, String name, Address address) {
        this.age = age;
        this.name = name;
        this.address = address;
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

    public User(int age, String name) {
        this.age = age;
        this.name = name;
    }

    // 浅拷贝的实现 （修改u2的age不影响u1）
    // @Override
    // public User clone() {
    // try {
    // // 调用Object的clone()，返回当前对象的浅拷贝副本
    // return (User) super.clone();
    // } catch (CloneNotSupportedException e) {
    // // 因已实现Cloneable，理论上不会走到这里，可转成运行时异常抛出
    // throw new RuntimeException(e);
    // }
    // }

    // 深拷贝
    @Override
    public User clone() {
        try {
            // 先做浅拷贝
            User cloneUser = (User) super.clone();
            // 步骤3：手动克隆引用类型字段（深拷贝的关键）
            cloneUser.address = this.address.clone();
            return cloneUser;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

// 步骤1：让引用类型Address也实现Cloneable
class Address implements Cloneable {
    private String city;

    public Address(String city) {
        this.city = city;
    }

    // 步骤2：Address重写clone()
    @Override
    public Address clone() {
        try {
            return (Address) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    // Getter/Setter
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}