package javase.re015;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        // region lambda
        // 简化匿名内部类的书写
        Integer[] arr = { 1, 3, 6, 4, 0 };
        Arrays.sort(arr, (a1, a2) -> a1 - a2); // 升序
        Arrays.sort(arr, (a1, a2) -> a2 - a1); // 降序
        Arrays.sort(arr, Comparator.naturalOrder()); // 自然升序
        Arrays.sort(arr, Comparator.reverseOrder()); // 自然降序
        // endregion

        // region 函数式编程
        // 强调做什么 而不是谁去做
        // Function<T, R>是 Java 8 引入的函数式接口，
        // 位于java.util.function包下，
        // 核心作用是封装一个 “输入类型为 T，输出类型为 R” 的函数逻辑，
        // 并支持函数的组合、链式调用。
        // R apply(T t)：
        // 接收一个 T 类型的参数，返回一个 R 类型的结果，是Function的核心方法，用于执行具体的函数逻辑。
        // andThen(Function<? super R, ? extends V> after)
        // 先执行当前Function的apply，再将结果传入after的apply（链式执行）
        // func1.andThen(func2).apply(t) → 先执行 func1，再执行 func2
        // compose(Function<? super V, ? extends T> before)
        // 先执行before的apply，再将结果传入当前Function的apply（反向链式）
        System.out.println("========== Function<> 基础使用 ==========");
        // 1. 基础使用：定义一个“整数转平方”的Function
        Function<Integer, Integer> squareFunc = num -> num * num;
        // 调用apply执行函数逻辑
        Integer square = squareFunc.apply(5);
        System.out.println("5的平方：" + square); // 输出25

        // 2. 数组元素批量处理：用Function处理Integer[]的每个元素
        // 定义“整数加10”的Function
        Function<Integer, Integer> addTenFunc = num -> num + 10;
        // 对数组中每个元素应用addTenFunc
        Integer[] newArr = handleArray(arr, addTenFunc);
        Integer[] newArr1 = handleArray(arr, (a1) -> a1 + 10);  // lambda

        System.out.println("数组元素加10后：" + Arrays.toString(newArr)); // [16, 14, 13, 11, 10]

        // 3. 函数组合：andThen（先平方，再加10）
        Function<Integer, Integer> squareAndAddTen = squareFunc.andThen(addTenFunc);
        System.out.println("5先平方再加10：" + squareAndAddTen.apply(5)); // 25+10=35

        // 4. 函数组合：compose（先加10，再平方）
        Function<Integer, Integer> addTenAndSquare = squareFunc.compose(addTenFunc);
        System.out.println("5先加10再平方：" + addTenAndSquare.apply(5)); // 15²=225

        // 5. 恒等函数：identity()（输入=输出）
        Function<Integer, Integer> identityFunc = Function.identity();
        System.out.println("恒等函数处理5：" + identityFunc.apply(5)); // 5

        // 6. 复杂函数逻辑：判断奇偶并返回对应字符串
        Function<Integer, String> oddEvenFunc = num -> {
            if (num % 2 == 0) {
                return num + "是偶数";
            } else {
                return num + "是奇数";
            }
        };
        System.out.println(oddEvenFunc.apply(6)); // 6是偶数
        System.out.println(oddEvenFunc.apply(7)); // 7是奇数
        // endregion
    }

    /**
     * 通用数组处理方法：用Function处理Integer[]的每个元素
     * 体现函数式编程“强调做什么（传入处理逻辑），而非谁去做（无需写具体循环）”
     * 
     * @param arr  待处理的数组
     * @param func 处理每个元素的Function逻辑
     * @return 处理后的新数组
     */
    public static Integer[] handleArray(Integer[] arr, Function<Integer, Integer> func) {
        Integer[] result = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            // 调用Function的apply方法处理每个元素，无需关心具体处理逻辑
            result[i] = func.apply(arr[i]);
        }
        return result;
    }

}
