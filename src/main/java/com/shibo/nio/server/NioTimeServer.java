package com.shibo.nio.server;

/**
 * @author by shibo on 2020/5/11.
 */
public class NioTimeServer {
    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-TimeServer-001").start();
    }
}
