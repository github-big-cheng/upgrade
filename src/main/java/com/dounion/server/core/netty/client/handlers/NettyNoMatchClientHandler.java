package com.dounion.server.core.netty.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyNoMatchClientHandler extends SimpleChannelInboundHandler<HttpMessage> {


    private final static Logger logger = LoggerFactory.getLogger(NettyNoMatchClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpMessage msg) throws Exception {
        // It should never run to here
        logger.warn("no handler found");
        ReferenceCountUtil.release(msg);
    }
}
