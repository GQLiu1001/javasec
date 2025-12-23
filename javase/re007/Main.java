package javase.re007;

public class Main {
    public static void main(String[] args) {
        // region 集合
        // ArrayList
        // 底层为动态数组
        // 初始容量 10 扩容 1.5 倍 balabala
        // add(E e)尾部添加元素均摊 O(1) 如需扩容 O(n)
        // remove(int index)根据索引删除元素 O(n) 需将index后的所有元素向前移位，覆盖被删除元素，移位操作与元素数量成正比
        // set(int index, E element)修改指定索引的元素 O(1) 数组是连续内存，通过索引可直接定位元素地址，赋值操作仅需常数时间
        // get(int index)根据索引获取元素 O(1) 数组随机访问特性，直接通过索引定位元素
        // indexOf(Object o)查找元素首次出现的索引 O(n) 从数组头部开始顺序遍历，直到找到元素或遍历结束
        // contains(Object o)判断集合是否包含指定元素 O(n) 底层调用indexOf(o)，遍历数组判断
        // endregion
    }
}
