package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author RabbitTank
 * @date 2025/12/27
 * @description Selector 是统一监听所有 Channel 的“事件管理员”；
 * selectedKeys() 是一批已经就绪、等你处理的“事件任务队列”；
 * 你在事件循环里一次取一个任务，处理完就从队列里移除，
 * 下一次 select() 只会把“新发生的事件”再放进来。
 */
public class ServerWithSelector {
    // Selector 是否触发 isWritable()，只和 interestOps 有关，和 attach 完全无关。
    // attach(buffer) 只是为了在“下次写事件到来时，能拿到没写完的数据”，不参与事件判定。
    // attach(buffer) 可以保存“没写完的数据”，下次写时继续用
    // ✅ 只要关注了 OP_WRITE，且 socket 当前可写，Selector 就会触发写事件
    // ❌ buffer 里还有数据 ≠ socket 当前可写
    // ✅ “当前可写”只取决于：操作系统里这个 socket 的「发送缓冲区」有没有空位
    // 当前可写（writable）= 操作系统的 socket 发送缓冲区（send buffer）中，还有可用空间
    //
    // 只要还有空间，内核就认为这个 socket 是 writable。

    // 可能结果 B：没写完（半包 / 背压）  buffer.hasRemaining() = true
    // 内核状态 socket send buffer：满了（或接近满）
    // 程序员介入
    //【Java】
    //ByteBuffer：还有数据
    //SelectionKey：
    //  interestOps = OP_READ | OP_WRITE
    //  attachment  = buffer
    //
    //【内核】
    //socket send buffer：满 ❌

    //对端 recv + ACK
    //↓
    //send buffer 释放空间

    // Selector 触发写事件
    //interestOps 包含 OP_WRITE
    //AND
    //send buffer 可写

    //如果写完了还不取消 OP_WRITE
    //↓
    //send buffer 通常一直可写
    //↓
    //select() 立即返回
    //↓
    //CPU 空转 100%
    //✅ 只有在「非阻塞写」这个场景下，你才需要考虑 send buffer、OP_WRITE、ACK 这些东西
    //
    //❌ 阻塞写 / 一次能写完的写，完全不用想这些
    public static void main(String[] args) throws IOException {
        // 创建 Selector
        Selector selector = Selector.open();
        // 公共的 buffer 区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 创建非阻塞客户端
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        // 关联 selector 和 ssc
        // SelectionKey 事件发生后 可以由他知道 是 哪个 channel 发生的
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // 事件类型：accept connect read write
        // sscKey 只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 这里的 select 监听所有的连接，一次性的 没有事情发生会阻塞 有事件才会恢复运行
            // select 在事件未处理时 他不会阻塞
            selector.select();
            // 处理事件 首先拿到事件集 所有可读可写可连接的事件集
            // selectedKeys() 会向里面加入不断发生的事件 但是他不会自己删除 所以需要后面的iterator.remove();
            // 不然会 NPE
            // 一次 select 醒来：可能同时有很多个 key
            // selectedKeys() 像一个“待处理事件队列”，不是历史记录；
            // 你消费一个、删一个，select 只负责把“新到的就绪事件”补充进来。
            Set<SelectionKey> keys = selector.selectedKeys();
            // 涉及到删除操作 不能用增强 for 遍历 需要 迭代器
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                // 这个 key 就是监听 accept 事件的  sscKey
                SelectionKey key = iterator.next();
                // 处理 key 的 时候要从 selectedKeys() 中删除 不然上一个会报错
                iterator.remove();
                // 区分事件类型
                if (key.isAcceptable()) {
                    // 通过 key 建立连接
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer bf = ByteBuffer.allocate(1024);
                    // 创建一个 key 管理 这个 accept 的 channel
                    // 第三个参数是 附件
                    sc.register(selector, SelectionKey.OP_READ, bf);
//                    scKey.interestOps();
                } else if (key.isReadable()) {
                    try {
                        // 如果是可读事件
                        // 先拿到 channel
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 读到 buffer
                        // 公共一个 buffer，多个连接轮流用
                        // demo 能跑，但真实情况下会遇到粘包/半包
                        // 可以将 ByteBuffer 通过附件关联到当前 selectKey上
                        ByteBuffer bf = (ByteBuffer) key.attachment();
                        int read = channel.read(bf);
                        // 如果客户端正常断开 read 返回值 -1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            // 处理消息边界 把消息按照分隔符拆分
                            // 最好是 第一个部分说明后面多大 4byte||||8byte||||||||
                            // 一次发送的数据超过 bytebuffer 会触发扩容
                            split(bf);
                            // 说明一个都没消耗掉
                            if (bf.position()==bf.limit()) {
                                // 需要扩容
                                ByteBuffer nbf = ByteBuffer.allocate(bf.capacity()*2);
                                bf.flip();
                                nbf.put(bf);
                                key.attach(nbf);
                            }
                        }
                    } catch (IOException e) {
                        // 客户端断开/异常时要把 key 从 selector 里取消掉
                        // 最后一次客户端断开 会抛异常 从 selector 中删除
                        e.printStackTrace();
                        key.cancel();
                    }
                }
            }
        }

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
