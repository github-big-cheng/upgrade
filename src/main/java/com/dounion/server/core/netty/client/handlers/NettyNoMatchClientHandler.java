package com.dounion.server.core.netty.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMessage;

public class NettyNoMatchClientHandler extends SimpleChannelInboundHandler<HttpMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpMessage msg) throws Exception {
        System.out.println("no handler found");
    }
}
