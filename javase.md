# Javase 查缺补漏

## 结构与入口
- 一个 `.java` 文件可有多个 `class`，但只能有一个 `public` 且类名必须与文件名一致
- 同一包下每个类都可以有 `main` 方法，运行入口可任选
- 顶级类不能用 `static` 修饰，`static` 仅用于内部类

## static / this / 初始化
- `static` 成员属于类，类加载即存在；实例成员属于对象
- `static` 方法只能直接访问 `static` 成员/方法；访问实例成员必须先 `new` 对象，且 `static` 中无 `this`
- `static` 代码块只执行一次，用于类级初始化；`static final` 常量建议全大写

## 基础语法与类型
- 方法重载只看参数列表（个数/类型/顺序），不看返回值
- 基本类型自动提升顺序：`byte/short/char -> int -> long -> float -> double`
- 引用类型强转看运行时真实类型，否则 `ClassCastException`
- `+` 拼接字符串存在编译期常量折叠；`char` 参与运算会提升为 `int`

## 字符串与相等性
- 字面量在常量池；`new String()` 会在堆里再建对象
- `==` 比地址，`equals` 比内容
- `StringBuilder` 非线程安全，`StringBuffer` 线程安全但有锁开销

## 面向对象与继承/多态
- 单继承多层继承；子类构造默认先 `super()`
- 重写影响运行时多态；字段访问编译/运行都看左边
- 父类引用不能直接调用子类特有方法（需向下转型 + `instanceof`）

## 接口 / 抽象类 / 工厂
- 抽象类描述“是什么”，接口描述“能做什么”
- 接口字段隐式 `public static final`；可有 `default`/`private`/`static` 方法（JDK8/9）
- 私有构造器 + 静态工厂方法控制实例创建
- 简单工厂 + 构造器注入体现 DI

## 内部类 / 匿名类 / Lambda
- 成员内部类通过外部对象创建；可访问外部私有成员
- 静态内部类不依赖外部对象，只能访问外部 `static` 成员
- 匿名内部类适合一次性实现；Lambda 仅能替代函数式接口
- 方法引用：`类名::静态方法`、`对象::实例方法`、`this::方法`

## 泛型 / 集合 / 可变参数
- `ArrayList` 动态数组，扩容 1.5 倍；中间插删 O(n)，随机访问 O(1)
- `List.of` 不可变；`Map.of` 不可重复且最多 10 对
- 可变参数底层是数组
- PECS：生产用 `extends`，消费用 `super`

## 函数式编程 / Stream
- `Function<T,R>` 的 `apply/andThen/compose` 体现组合
- Stream 创建 → 中间操作（`filter/map/limit/skip/distinct`）→ 终结（`collect/forEach/count/toArray`）
- 双列集合先 `entrySet/keySet` 再 `stream`

## 并发与单例
- `wait/notify` 必须在 `synchronized` 内；用 `while` 防伪唤醒
- 懒汉单例：`synchronized` 方法版 vs DCL + `volatile`（可见性/禁止重排）

## JVM 与内存
- 栈/堆/方法区(元空间)/本地方法栈/寄存器职责要分清
- 对象在堆、引用在栈、字节码与静态成员在方法区

## IO/NIO（API略）
- 字节流/字符流、缓冲流、`try-with-resources` 自动关流
- NIO：`Channel/Buffer/Selector`；NIO2：`Path/Files`

## 其他易忘点
- `clone`：浅拷贝 vs 深拷贝（引用类型需手动克隆）
- `Runtime` 可看 CPU/内存并 `exec` 进程
- 反射：`Class` 获取、构造器/字段/方法访问与调用
- 字节序：`ByteBuffer` 支持大端/小端
- 正则提取数字、URL 参数解析
- 排序：冒泡/快排（分区 + 递归）
