package javase.re017;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // region 可变参数
        // 底层就是可变数组
        int local = sum(1,3,7,5,45,23,33);
        System.out.println(local);
        List<String> list = List.of("null","1");
        // List.of 创建的 list 不可变 以及 Xxx.of(T... args)
        // 对于 Map.of 元素不能重复 键值对最多 10 对 超过需要 ofEntries 或者直接使用 copyOf
        for (String l : list) {
            System.out.println(l);
        }
        // endregion

        // region instanceof
        // 对象引用 instanceof 类/接口类型
        // 判断一个对象的引用是否是某个类 / 接口的实例（包括子类 / 实现类）
        // endregion

        // region stream流
        // 结合 lambda 简化集合、数组的操作
        // 1.开启
        //  对于单列集合 直接 .stream()
        //  对于双列集合 先 keySet entrySet 再 .stream()
        //  对于数组 可以用 Arrays.stream(int[] arr)
        //  对于零散数据 Stream.of(T... args)
        // 2.中间方法
        // Stream<T> filter(Predicate<? super T> predicate) 过滤 留下符合要求的
        // Stream<T> map(Fuction<T,R> mapper) 转换数据类型 将输入的 T 转换为 R 
        // limit 获取前几个元素
        // skip 跳过前几个元素
        // distinct 去重
        // concat 合两个流
        // 3.终结方法
        // collect(Collector collector) 收集数据放入集合 List Set Map
        //  Collector.toList()
        //  Collector.toSet()
        //  Collector.toMap( 
        //          Fuction<? extends T,? extend R> kRule, // 键的规则 
        //          Fuction<? extends T,? extend R> vRule) // 值的规则
        // forEach(Consumer action) 遍历
        // count() 统计
        // toArray() 收集数据放入数组
        // endregion
        
        // region 方法引用
        // 便利引用 这个相当于用引用的函数重写函数式接口中的函数
        // 方法形参和返回值要和抽象方法的形参返回值一致
        // 引用静态方法 直接 类名::方法名
        // 引用其他类对象 其他类对象::方法名
        // 引用成员方法 本类 this::方法名 父类 super::方法名

    }
    static int sum(int... args){
        int sum = 0;
        for (int i = 0; i < args.length; i++) {
            sum += args[i];
        }
        return sum;
    }
}
