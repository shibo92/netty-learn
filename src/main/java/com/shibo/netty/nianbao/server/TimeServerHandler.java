package com.shibo.netty.nianbao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * @author by shibo on 2020/4/30.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    private int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ByteBuf buf = (ByteBuf) msg;
        // byte[] req = new byte[buf.readableBytes()];
        // buf.readBytes(req);
        // String body = new String(req, "UTF-8");
        // 有了 StringDecoder 解码器，不需要转byte
        String body = (String) msg;
        System.out.println("服务器接收到消息体：" + body + "；counter = " + ++counter);
        String resultToClient = "QUERY TIME".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "bad order!!!";
        resultToClient = resultToClient + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(resultToClient.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // netty的write方法并不直接将消息写入SocketChannel中，而是写入到缓冲区
        // 当调用flush时，才会将缓冲区内容写入SocketChannel
        System.out.println("flush");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("error:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}

