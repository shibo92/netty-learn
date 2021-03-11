package com.shibo.netty.nianbao.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * @author by shibo on 2020/4/30.
 */

public class TimeClientHandler extends ChannelHandlerAdapter {
    private byte[] req;
    private int counter;
    public TimeClientHandler() {
        // req = "QUERY TIME".getBytes();
        // 添加换行符，不然 LineBasedFrameDecoder 解码器不识别
        req = ("QUERY TIME" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf firstMessage;
        for (int i = 0; i < 10; i++) {
            firstMessage = Unpooled.buffer(req.length);
            firstMessage.writeBytes(req);
            ctx.writeAndFlush(firstMessage);
        }
    }

    /**
     * 接收返回信息
     * @param ctx
     * @param msg
     * @throws UnsupportedEncodingException
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        /*ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");*/

        // 有了 StringDecoder 解码器，不需要转byte
        String body = (String) msg;
        System.out.println("客户端接收到消息体：" + body + "；counter =" + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("error:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
