package javase.re003;

public class Main {
    public static void main(String[] args) {
        // region 数组
        int[] arr1 = new int[]{1,2,3};
        int[] arr2 = {1,2,3,4};
        System.out.println(arr1[2]);
        System.err.println(arr2[0]);
        System.out.println(arr1); 
        // [I@5acf9800
        // [ 代表是一个数组
        // I 代表着存储的是 int
        // @ 分隔符
        // 5acf9800 十六进制地址
        // endregion
        
    }
}
