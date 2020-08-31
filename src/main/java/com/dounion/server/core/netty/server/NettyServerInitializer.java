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
            // url : like *.css|*.js|*.png|*.jpg some others, see detail in StringHelper.isStaticRequest
            .addLast(new NettyStaticFileServerHandler()) // 静态资源请求
            // url : /download/
            .addLast(new NettyDownloadServerHandler()) // 文件下载请求
            // method GET
            .addLast(new NettyGetRequestServerHandler()) // GET请求
            // method POST
            .addLast(new ChunkedWriteHandler()) // 大文件的上传
            .addLast(new NettyPostRequestServerHandler()) // POST请求
            // all request not matched return 404
            .addLast(new NettyNotFoundServerHandler()) // 404
        ;
    }

}
