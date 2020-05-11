package com.shibo.nio.server;

/**
 * @author by shibo on 2020/5/11.
 */
public class NioTimeServer {
    public static void main(String[] args) {
        new Thread(new MultiplexerTimeServer(8080), "NIO-TimeServer-001").start();
    }
}
