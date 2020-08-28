package com.dounion.server.core.netty.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec())
//            .addLast(new HttpObjectAggregator(512*1024))
            .addLast(new NettyStaticFileServerHandler()) // 静态资源请求
            .addLast(new NettyGetRequestServerHandler()) // GET请求
            .addLast(new NettyPostRequestServerHandler()) // POST请求
        ;
    }

}
