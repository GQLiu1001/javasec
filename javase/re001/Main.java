package javase.re001;
import java.util.Scanner;

public class Main {
    // java P30-P38
    public static void main(String[] args) {
        // region java的运算符
        // 注意点1:小数加减需要BigDecimal
        // 注意点2:取模 % 得到余数
        System.out.println(10.1 % 3);
        // endregion
        // region 键盘录入 递归
        System.out.println("输入一个数");
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        slice(num);
        // endregion

        // region 类型转换 自动转换
        // 取值范围小 -> 自动升级为 取值范围大的
        // byte -> short -> int -> long -> float -> double
        // 注意点：byte short char 会自动提升为 int 进行计算
        for (int i = 0; i < 2; i++) {
            byte a = 1;
            short b = 2;
            char c = '1';
            int d = a + b + c;
            System.out.println(d);
        }
        // endregion
        // region 类型转换 强制转换
        // 引用类型的强制类型转换本质上就是看“运行时真实对象是什么类
        Object o = "hi"; // 真实对象：String
        String s = (String) o; // ✅ 运行时是 String，所以成功

        Object o2 = 123; // 真实对象：Integer（自动装箱）
        // Long l = (Long) o2; // ❌ ClassCastException（真实是 Integer 不是 Long）
        Integer i1 = 1;
        Long l2 = i1.longValue();
        
        Long l1 = Long.valueOf(i1);
        System.out.println("l1 = " + l1);

        // endregion

        // region 字符串运算符
        // + 为拼接 如果连续则从左到右
        String a = 1+99+"hello";
        System.out.println(a); // 100hello
        String b = a + "hello";
        System.out.println(b); // ahello
        // endregion

        // region 字符运算
        // 注意点：非常常用的技巧 ASCII码 A(65) ... Z ... a(97) ... z 
        System.out.println('a' - 'a'); // 0
        System.out.println( 1 + 'a'); // 98 (算术运算的二元数值提升 自动升级到int)
        System.out.println( "a" + 'a' ); // aa
        // endregion

        // region 自增自减  
        // 注意 a++ 是 先用再加 ++a 是 先加再用
        // endregion

        // region 逻辑运算符
        // & 与 && 短路与
        // | 或 || 短路或
        // ^ 异或 这个算法重要 对比二进制，位相同为 0，不同为 1
        // ! 非 取反
        // endregion
    }

    private static void slice(int num) {
        if (num == 0)
            return;
        System.out.println(num % 10);
        slice(num / 10);
    }
}
