package com.dounion.server.core.netty.handlers;

import com.dounion.server.core.helper.StringHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyGetRequestServerHandler extends NettyHttpRequestServerHandler {

    private Logger logger = LoggerFactory.getLogger(NettyGetRequestServerHandler.class);

    @Override
    protected Boolean isMatch(Object msg) {
        if(msg instanceof HttpRequest){
            this.request = (HttpRequest) msg;
        }
        return this.request != null &&
                    this.request.method().equals(HttpMethod.GET) &&
                    !StringHelper.isStaticRequest(request.uri());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(!this.isMatch(msg)){
            logger.debug("NettyGetRequestServerHandler not match..{}", msg);
            ctx.fireChannelRead(msg);
            return;
        }

        if(msg instanceof HttpRequest){
            super.convertUrlParams();
        }

        if(msg instanceof LastHttpContent){
            super.writeResponse(ctx);
        }
    }
}
