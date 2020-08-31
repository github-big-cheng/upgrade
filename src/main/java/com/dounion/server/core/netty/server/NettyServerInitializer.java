package com.dounion.server.core.netty.server;

import com.dounion.server.core.netty.server.handlers.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestDecoder()) // 解码
            .addLast(new HttpContentCompressor()) // 压缩
            .addLast(new HttpResponseEncoder()) // 编码
            .addLast(new NettyStaticFileServerHandler()) // 静态资源请求
            .addLast(new NettyDownloadServerHandler()) // 文件下载请求
            .addLast(new NettyGetRequestServerHandler()) // GET请求
            .addLast(new ChunkedWriteHandler()) // 大文件的上传
            .addLast(new NettyPostRequestServerHandler()) // POST请求
            .addLast(new NettyNotFoundServerHandler()) // 404
        ;

//        pipeline.addLast(new HttpRequestDecoder());
//        pipeline.addLast(new HttpResponseEncoder());
//        pipeline.addLast(new HttpContentCompressor());
//        pipeline.addLast(new HttpUploadServerHandler());
    }

}
