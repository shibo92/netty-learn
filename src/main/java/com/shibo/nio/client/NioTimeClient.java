package com.shibo.nio.client;

/**
 * @author by shibo on 2020/5/11.
 */
public class NioTimeClient {
    public static void main(String[] args) {
        new Thread(new NioTimeClientHandler("localhost",8080), "NIO-TimeClient-001").start();
        // new Thread(new NioTimeClientHandler("localhost",8080), "NIO-TimeClient-002").start();
    }
}
