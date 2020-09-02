package com.dounion.server.core.netty.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * it's sadly to this handle
 *      no resource found in this service
 */
public class NettyNotFoundServerHandler extends NettyHttpRequestServerHandler {

    private final static Logger logger = LoggerFactory.getLogger(NettyNotFoundServerHandler.class);

    @Override
    protected Boolean isMatch(Object msg) {
        // all requests can be matched
        return true;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        HttpUtil.setContentLength(response, 0);
        // you can response a 404.html by String or File here

        ctx.writeAndFlush(response);
    }
}
