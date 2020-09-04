package com.dounion.server.core.netty.client;

import com.dounion.server.core.netty.client.handlers.NettyFileClientHandler;
import com.dounion.server.core.netty.client.handlers.NettyMessageClientHandler;
import com.dounion.server.core.netty.client.handlers.NettyNoMatchClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestEncoder()) // 编码
                .addLast(new HttpResponseDecoder()) // 解码
//                .addLast(new HttpObjectAggregator(1024 * 10 * 1024)) // 聚合
                .addLast(new HttpContentDecompressor()) // 解压
                .addLast(new ChunkedWriteHandler()) // 大数据
                .addLast(new NettyFileClientHandler()) // 文件下载
                .addLast(new NettyMessageClientHandler()) // 普通请求
                .addLast(new NettyNoMatchClientHandler()) // 未处理的响应
        ;
    }

}
