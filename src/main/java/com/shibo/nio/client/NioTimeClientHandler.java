package com.shibo.nio.client;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author by shibo on 2020/5/11.
 */
public class NioTimeClientHandler implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public NioTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop) {
            try {
                selector.select(1000);
                // 多路复用器轮训准备就绪的key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 判断是否连接成功
            SocketChannel sc = (SocketChannel) key.channel();
            // 接收connect事件处理
            if (key.isConnectable()) {
                // 如果连接成功，注册读事件到多路复用器，
                if (sc.finishConnect()) {
                    doWrite(sc);
                    sc.register(selector, SelectionKey.OP_READ);
                } else {
                    System.out.println("连接失败，进程退出");
                    System.exit(1);
                }
            }
            if (key.isReadable()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                // 异步读取服务端返回的消息到缓冲区
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("now is " + body);
                    this.stop = true;
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                } else {
                    System.out.println("client 读到0字节");
                }
            }
        }
    }


    private void doConnect() throws IOException {
        // 异步连接服务端
        boolean isConnected = socketChannel.connect(new InetSocketAddress(host, port));
        if (isConnected) {
            // 判断是否连接成功，成功则注册读状态到多路复用器
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            // 如果没有连接成功（异步连接，返回false，说明客户端已经发送sync包，服务器没有返回ack，物理链路还没建立）
            // 则注册CONNECT状态到多路复用器，监听服务器ACK应答
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        // 调用write异步发送消息给服务端
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            System.out.println("send order to server success.");
        }
    }
}
