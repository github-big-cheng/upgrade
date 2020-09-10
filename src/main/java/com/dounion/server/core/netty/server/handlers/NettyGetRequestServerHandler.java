package com.dounion.server.core.netty.server.handlers;

import com.dounion.server.core.request.MappingConfigHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyGetRequestServerHandler extends NettyHttpRequestServerHandler {

    private final static Logger logger = LoggerFactory.getLogger(NettyGetRequestServerHandler.class);

    @Override
    protected Boolean isMatch(Object msg) {
        if(msg instanceof HttpRequest){
            this.request = (HttpRequest) msg;
        }
        return this.request != null &&
                    // GET请求
                    this.request.method().equals(HttpMethod.GET) &&
                    // 已注册的服务
                    MappingConfigHandler.isMapping(request.uri());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(!this.isMatch(msg)){
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if(msg instanceof HttpRequest){
                super.convertUrlParams();
            }

            if(msg instanceof LastHttpContent){
                super.writeResponse(ctx);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
