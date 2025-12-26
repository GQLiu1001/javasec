package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// clear() 不是 “清空数据”，是重置 ByteBuffer 的指针，回到 “可以被写入数据” 的写模式状态；
// 代码里的 “写操作” 是channel.read(buffer)（往 buffer 里写数据），不是你主动调用put()；
// clear() 的核心作用：为下一次channel.read(buffer)（写操作）腾出 “可写空间”，避免指针位置错误导致写不进数据。
public class Main {
    public static void main(String[] args) throws IOException {

        // 1. 打开文件通道（try-with-resources 自动关闭资源）
        try (FileChannel channel = new FileInputStream("mynetty/data.txt").getChannel()) {
            // 2. 分配1024字节的缓冲区（默认是写模式：position=0，limit=1024）
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // 3. 从通道读取数据到缓冲区（返回实际读取的字节数）
            // channel.read( new ByteBuffer[]{b1,b2,b3}) 可以分写
            int readBytes = channel.read(buffer);
//            System.out.println("实际读取字节数 = " + readBytes);

            if (readBytes == -1) { // 读取到文件末尾的标志
                System.out.println("文件为空");
                return;
            }
            // 这一步是必要的（因为channel.read后是写模式）
            // 4. 切换缓冲区为读模式（limit=当前position，position=0）
            buffer.flip();

            // 5. 循环读取缓冲区中的数据
//            System.out.println("文件内容：");
//            while (buffer.hasRemaining()) {
//                byte b = buffer.get(); // 每次读取1个字节
//                System.out.print((char) b); // 直接打印字符，而非逐行输出（更易读）
//            }
            // 切换为读模式后
//            byte[] data = new byte[10]; // 每次读10个字节
//            while (buffer.remaining() > 0) {
//                int readLen = Math.min(10, buffer.remaining()); // 避免剩余字节不足10个时越界
//                // offset 就是「往目标数组里写数据时，从哪个位置开始写」 offset=0 是最常用的写法，表示从数组开头写入数据；
//                buffer.get(data, 0 , readLen); // 批量读取readLen个字节
//                System.out.println("批量读取10字节（实际读" + readLen + "）：" + new String(data, 0, readLen));
//            }

            // 切换为 写 模式
            buffer.clear(); // 切回写模式，准备put数据

            // 字符串转为 ByteBuffer
            String s = "Hello";
            buffer.put(s.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte b1 = buffer.get();
                System.out.println("b1 = " + b1);
            }



        }
        // Charset 和 wrap 自动就是切换到读模式了
        // 只有当你手动往 ByteBuffer 写数据（如 put()、channel.read()） 后，需要切换到读模式时，才需要 flip()；
        String s = "Hello";
        // Charset
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode(s);
        System.out.println("=== UTF_8.encode创建的buffer1 ===");
        // 打印指针状态，验证读模式（可选）
        System.out.printf("buffer1 - position=%d, limit=%d%n", buffer1.position(), buffer1.limit());
        while (buffer1.hasRemaining()) {
            byte b2 = buffer1.get();
            System.out.println("b = " + b2);
        }
        // wrap
        ByteBuffer buffer2 = ByteBuffer.wrap(s.getBytes());
        System.out.println("=== wrap创建的buffer2 ===");
        // 打印指针状态，验证读模式（可选）
        System.out.printf("buffer2 - position=%d, limit=%d%n", buffer2.position(), buffer2.limit());
        while (buffer2.hasRemaining()) {
            byte b3 = buffer2.get();
            System.out.println("b = " + b3);
        }
    }
}