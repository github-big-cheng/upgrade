package com.dounion.server.core.netty.handlers;

import com.dounion.server.core.helper.StringHelper;
import com.dounion.server.core.request.HandlerMappingConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NettyHttpRequestServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Logger logger = LoggerFactory.getLogger(NettyHttpRequestServerHandler.class);

    protected HttpRequest request;
    protected HttpData partialContent;
    protected HttpPostRequestDecoder decoder;
    protected Map<String, Object> params = new HashMap<>();

    protected static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("NettyHttpRequestServerHandler error:{}", cause);

        ByteBuf byteBuf = Unpooled.copiedBuffer(cause.getMessage(), CharsetUtil.UTF_8);
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, byteBuf);
        ctx.writeAndFlush(response);
    }

    protected abstract Boolean isMatch(Object msg);

    protected void writeResponse(ChannelHandlerContext ctx) throws Exception {
        String uri = StringHelper.getRealPath(request.uri());
        logger.debug("request:{}", request);
        logger.debug("handlerMethod --> uri:[{}], path:[{}], params:[{}]", request.uri(), uri, this.params);
        ChannelFuture future =
                ctx.writeAndFlush(HandlerMappingConfig.handlerMethod(uri, this.params));
        if(this.request!=null || this.decoder!=null || !this.params.isEmpty()){
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
        // destroy the decoder to release all resources
        if(decoder != null){
            decoder.destroy();
        }
        decoder = null;
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
        if (HttpMethod.GET.equals(this.request.method())) {
            return params;
        }

        decoder = new HttpPostRequestDecoder(factory, this.request);

        return params;
    }

}
