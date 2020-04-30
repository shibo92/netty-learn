package com.shibo.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Date;

/**
 * @author by shibo on 2020/4/30.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("服务器接收到消息体：" + body);
        String result = "QUERY TIME".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "bad order!!!";
        ByteBuf resp = Unpooled.copiedBuffer(result.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // netty的write方法并不直接将消息写入SocketChannel中，而是写入到缓冲区
        // 当调用flush时，才会将缓冲区内容写入SocketChannel
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

