package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        // 无 selector 版本
        // 阻塞模式 单线程
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 1.建立服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); // 切换为非阻塞模式
        // 2.绑定服务器监听端口
        ssc.bind(new InetSocketAddress(8080));
        // 连接集合
        List<SocketChannel> channels = new ArrayList<SocketChannel>();
        while (true) {
            // 3.accept 建立与客户端连接 SocketChannel 用来与客户端之间通信
            // 这里是阻塞模式 如果没有接受 会一直等待 (监听延迟队列)
            // 必须要有新连接才能刷新消息
            // 非阻塞模式 没有连接建立会直接返回 null
            SocketChannel sc = ssc.accept();
            // 防止乱加 null
            if (sc != null) {
                sc.configureBlocking(false); // 设置非阻塞 read
                channels.add(sc);
            }
            for (SocketChannel c : channels) {
                // 接受客户端发送的数据
                // 也是阻塞模式 线程会停止运行 不会占用 cpu 时间
                // 可设置非阻塞 线程运行 没读到数据 返回 0
                int read = c.read(buffer);
                if (read > 0) {
                    // buffer 切换读模式
                    buffer.flip();
                    // 读buffer之后
                    buffer.clear();
                }
            }

        }
    }
}
