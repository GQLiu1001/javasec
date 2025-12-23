package javase.re002;
public class Main {
    // java P39-P53
    public static void main(String[] args){
        // region 流程控制
        // System.out.println("!");
        // if
        // switch
        // JDK12 新特性
        int num = 1;
        switch(num) {
            case 1 -> System.out.println("匹配到1");
            case 2,3,4,5 -> {
                System.out.println("a");
            }
            default -> System.out.println("默认");
        };

        System.out.println();
        // for
        for (int i = 1; i <= 9 ; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print(i + "*" + j + "=" + (i*j) + "\t");
            }
            System.out.println();
        }
        

        // while 
        // endregion
    }

}
