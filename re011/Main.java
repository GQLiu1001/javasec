
package re011;

public class Main {

    public static void main(String[] args) {
        // region 包
        // 包名加类名 => 全限定名
        // endregion
        
        // region final
        // final 修饰
        //          方法 -> 不能被重写
        //          类   -> 不能被继承
        //          变量 -> 常量，只能被赋值一次
        //
        //  基础数据类型 一旦完成初始化，就无法再更改
        //  引用数据类型 不能改变引用的对象，但是可以改变其内容
        //      
        // 常量的规则 ALLCAP _ 连接 MIN_NAME
    
    
        // region 权限修饰符
        // public private protected
        // 范围： private < 默认 < protected < public
        //   private:同一个类中用
        //   默认   :同一个类、同一个包的其他类
        //   protected:同一个类、同一个包的其他类、不同包下的子类
        //   public: 同一个类、同一个包的其他类、不同包下的子类、不同包下的无关类
    
        // 开发规范：成员变量私有private、方法公开public
        // endregion
        
        // region 代码块
        // 局部代码块 静态代码块
        // 局部代码块可以提前结束生命周期 白雪被淘汰了
        // 构造代码块优先于构造方法
        {
            int a = 1;
            System.out.println(a);
        }
        // System.out.println(a);
        // 静态代码块

        Cls a = new Cls();
        System.out.println(a);

    
    }
}

class Cls{
    static int a;

    static {
        // 静态代码块数据初始化
        // 随着类加载而加载，只执行一次
        a = 1;
        System.out.println("静态代码块执行a");
        
    }
}

// endregion