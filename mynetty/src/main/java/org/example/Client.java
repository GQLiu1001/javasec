package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        System.out.println("waiting for client connection");
        sc.connect(new InetSocketAddress("localhost",8080));
        System.out.println("client connected");
        System.out.println("sending data");
        sc.write(Charset.defaultCharset().encode("hello"));
        System.out.println("sending data over");
    }
}
