package re018;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // region File
        // 根据路径创建 File 对象
        File f1 = new File("路径");
        // f1.一堆方法
        // 主要是对文件以及文件夹进行操作
        // endregion

        // region IO
        // NIO BIO
        // IO 流 输入流、输出流
        // 字节流、字符流(只能处理纯文本文件)
        // 字节流 InputStream OutputStream 字符流 Reader Writer 均为接口

        // region 字节流
        // 字节流 InputStream -> FileInputStream 读
        // read 方法 一次读一个字符 返回 ASCII 然后指针向后移动 最后一个为 -1 结束标记
        // 字节流 OutputStream -> FileOutputStream 写
        // write 方法的参数是整数 对应 ASCII
        // write 的 换行：打印换行符 直接 String warp = "\r\n" 再写入即可
        // write 的 续写：直接最后参数为 true
        FileInputStream fis = new FileInputStream(f1);
        FileOutputStream fos = new FileOutputStream(f1);
        // 拷贝文件 关键：边读边写
        int b;
        while ((b = fis.read()) != -1) {
            fos.write(b); // 一次读一个字节
        }
        // 可以读数组
        byte[] by = new byte[4];
        int len1 = fis.read(by); // len1 就是反回的4个字符的 ASCII

        int len;
        byte[] bytes = new byte[1024 * 1024 * 5];
        while ((len = fis.read(bytes)) != -1) {
            fos.write(bytes, 0, len); // 最后可能装不满4个
        }
        // 先开的最后关
        fos.close();
        fis.close();

        // try with resource 可以自动关流
        // try(
        // FileInputStream fis = new FileInputStream(f1);
        // FileOutputStream fos = new FileOutputStream(f1);){....}

        // FileInputStream fis = new FileInputStream(f1);
        // FileOutputStream fos = new FileOutputStream(f1);
        // try(fis,fos){...}
        // endregion

        // region 字符流
        // 字符流 = 字节流 + 字符集
        // 解码为二进制第一位是1就是中文，gbk就是读两个字节，Unicode就是读三个字节
        // FileReader
        // FileWriter
        // endregion

        // region buffered 缓冲流
        // 底层自带长度 8192 的缓冲区提高性能
        // 需要输入参数基本流 InputStream OutputStream
        // endregion

        // region 序列化流 Object
        // 写读对象
        // endregion

        // region 解压缩流
        // 打包解压
        // endregion

        // region NIO
        // IO 1 主打非阻塞网络 IO / 高性能文件读写，NIO 2 主打便捷的文件操作 / 异步 IO
        // NIO 1 是同步非阻塞 IO的核心，主打网络高并发和大文件高性能读写，
        // 核心组件是Channel（通道）、Buffer（缓冲区）、Selector（选择器）
        /*
         * Channel 双向 IO 通道（可读写），替代 BIO 的单向流；常见实现：SocketChannel（网络）、FileChannel（文件）
         * Buffer 字节缓冲区，替代 BIO 的字节数组；通过flip()/clear()手动管理读写状态
         * Selector 多路复用器，实现一个线程管理多个 Channel 的 IO 事件（核心：非阻塞网络 IO 的基础）
         */
        // NIO 2 是对 NIO 1 的补充，主打便捷的文件操作和异步 IO，日常开发中使用频率远高于 NIO 1 的网络编程
        // Netty代码本质是Java代码，调用Netty的API而已
        // Netty 与 Java 的关系，就像 Spring 与 Java 的关系
        /*
         * 本地小文件操作：用 BIO（或 Commons IO/Guava 封装的 BIO）；
         * 本地大文件操作：用 NIO 1 的FileChannel；
         * 简单网络通信：用 BIO；
         * 高并发网络编程：用 Netty；
         * 现代文件操作：用 NIO 2 的Path/Files。
         */
        // endregion
        // endregion
    }
}
