package juc.re001;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Main {

    static final Map<String, Integer> map1 = new HashMap<>(
            Map.ofEntries(
                    Map.entry("A", 1),
                    Map.entry("B", 2),
                    Map.entry("C", 3)));

    static final Map<String, Integer> map2 = new HashMap<>(
            Map.ofEntries(
                    Map.entry("D", 11),
                    Map.entry("E", 21),
                    Map.entry("F", 31)));

    public static void main(String[] args) throws Exception {
        Runnable run = () -> {
            System.out.println(map2.get("D"));
        };
        Thread t1 = new Thread(run);
        t1.start(); // 启动线程 去执行 run 方法
        Integer result = null;
        try {
            result = method();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("result=" + result);
        Callable<Integer> call = () -> map1.get("A");

        FutureTask<Integer> task = new FutureTask<>(call);
        Thread t2 = new Thread(task);
        t2.start();

        Integer result1 = task.get(); // 阻塞等待结果
        System.out.println("result=" + result1);

    }

    static Integer method() throws Exception {
        Callable<Integer> call = () -> {
            return map1.get("A");
        };
        return call.call();
    }
}
