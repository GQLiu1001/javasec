package org.example;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Exam {
    //    网络上有多条数据发送给服务端，数据之间使用\n进行分隔但由于某种原因这些数据在接收时，
    //    被进行了重新组合，例如原始数据有3条为
    //
    //    Hello,worldln
    //    I'm zhangsan\n
    //    How are you?\n
    //
    //    变成了下面的两个byteBuffer(黏包，半包)
    //
    //    Hello,world\nI'm zhangsan\nHow
    //    are you?\n
    //
    //    现在要求你编写程序，将错乱的数据恢复成原始的按\n分隔的数据大T
    public static void main(String[] args) {
        // 1. 初始化缓冲区，写入多行文本（模拟分段写入的场景）
        ByteBuffer source = ByteBuffer.allocate(64); // 扩容缓冲区避免溢出
        // 第一段数据：包含2个完整行 + 半个行
        source.put("Hello, world\nI'm zhangsan\nHo".getBytes(StandardCharsets.UTF_8));
        System.out.println("=== 第一次调用split，处理：Hello, world\\nI'm zhangsan\\nHo ===");
        split(source); // 第一次拆分

        // 第二段数据：补全上一个不完整行，新增一个完整行
        source.put("w are you?\nNice to meet you!\n".getBytes(StandardCharsets.UTF_8));
        System.out.println("\n=== 第二次调用split，补充：w are you?\\nNice to meet you!\\n ===");
        split(source); // 第二次拆分
    }

    /**
     * 按换行符 \n 拆分ByteBuffer中的内容，并打印拆分后的每一行
     * @param source 待拆分的源缓冲区（调用前为写模式，调用后自动切回写模式）
     */
    private static void split(ByteBuffer source) {
        // 1. 切换为读模式：准备读取源缓冲区的数据
        source.flip();

        // 2. 遍历缓冲区，找换行符 \n 拆分完整行
        for (int i = 0; i < source.limit(); i++) {
            // 找到换行符 = 找到一条完整行
            if (source.get(i) == '\n') {
                // 计算当前完整行的字节长度（从当前position到换行符的长度）
                int lineLength = i + 1 - source.position();
                // 创建新缓冲区存储这一行内容
                ByteBuffer target = ByteBuffer.allocate(lineLength);

                // 3. 从源缓冲区读取完整行数据，写入target
                for (int j = 0; j < lineLength; j++) {
                    target.put(source.get());
                }

                // 4. 输出拆分后的完整行（核心：转字符串展示）
                target.flip(); // target切换为读模式
                String line = StandardCharsets.UTF_8.decode(target).toString();
                System.out.println("✅ 拆分出完整行：" + line);
            }
        }

        // 5. 切换回写模式：保留未读完的不完整数据（比如最后没换行的部分），准备后续写入
        source.compact();
    }
}
