package com.shibo.netty.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author by Administrator on 2021/3/10.
 */
public class HttpFileServer {
    public void run(final int port, final String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                        channel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                        channel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                        channel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                        channel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler());
                    }
                });
        // 绑定端口，同步等待成功
        ChannelFuture f = b.bind(port).sync();
        // 等待服务端监听端口关闭
        f.channel().closeFuture().sync();

    }
}
