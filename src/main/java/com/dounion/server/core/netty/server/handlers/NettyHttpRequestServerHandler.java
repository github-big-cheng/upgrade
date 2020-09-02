package com.dounion.server.core.netty.server.handlers;

import com.dounion.server.core.helper.StringHelper;
import com.dounion.server.core.request.MappingConfigHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NettyHttpRequestServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    public NettyHttpRequestServerHandler() {
        super(false);
    }

    private Logger logger = LoggerFactory.getLogger(NettyHttpRequestServerHandler.class);

    protected HttpRequest request;
    protected Map<String, Object> params = new HashMap<>();


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("NettyHttpRequestServerHandler error:{}", cause);

        ByteBuf byteBuf = Unpooled.copiedBuffer(cause.getMessage(), CharsetUtil.UTF_8);
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, byteBuf);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    protected abstract Boolean isMatch(Object msg);

    protected void writeResponse(ChannelHandlerContext ctx) throws Exception {
        String uri = StringHelper.getRealPath(request.uri());
        logger.debug("handlerMethod --> uri:【{}】, path:【{}】, params:【{}】", request.uri(), uri, this.params);
        FullHttpResponse response = MappingConfigHandler.handlerMethod(uri, this.params);
        HttpUtil.setContentLength(response, response.content().readableBytes());
        ChannelFuture future =
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        if(this.request!=null || !this.params.isEmpty()){
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    reset();
                }
            });
        }
    }

    protected void reset() {
        request = null;
        params.clear();
    }


    protected Map<String, Object> convertUrlParams(){

        // url params
        QueryStringDecoder decoderQuery = new QueryStringDecoder(this.request.uri());
        Map<String, List<String>> uriAttributes = decoderQuery.parameters();
        for (Map.Entry<String, List<String>> attr: uriAttributes.entrySet()) {
            for (String attrVal: attr.getValue()) {
                params.put(attr.getKey(), attrVal);
            }
        }

        return params;
    }

}
