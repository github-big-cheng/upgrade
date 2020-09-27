package com.dounion.server.core.netty.client.handlers;

import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.netty.client.NettyResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.IllegalReferenceCountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNettyClientHandler<V> extends SimpleChannelInboundHandler<HttpObject> {

    protected Logger logger = LoggerFactory.getLogger(AbstractNettyClientHandler.class);

    protected HttpRequest request;
    protected HttpResponse response;
    protected NettyResponse<V> nettyResponse;

    /**
     * 是否匹配
     * @param ctx
     * @return
     */
    protected abstract boolean isMatch(ChannelHandlerContext ctx);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // 初始化响应
        this.nettyResponse =
                (NettyResponse<V>) ctx.channel().attr(NettyClient.NETTY_CLIENT_RESPONSE).get();
        logger.trace("nettyResponse: {}", this.nettyResponse);

        // 初始化请求对象
        this.request = (HttpRequest) ctx.channel().attr(NettyClient.NETTY_CLIENT_REQUEST).get();
        if(this.request == null){
            this.nettyResponse.setError(new SystemException("request init failed..."));
            return;
        }
        logger.trace("ctx.writeAndFlush:.......{}", this.request);
        ctx.writeAndFlush(this.request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(!(cause instanceof IllegalReferenceCountException)){
            logger.error("netty client error:{}", cause);
            this.nettyResponse.setError(cause);
            ctx.channel().close();
        }
    }

}
