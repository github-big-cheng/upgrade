package com.dounion.server.core.netty.client.handlers;

import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.netty.client.NettyResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyMessageClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Logger logger = LoggerFactory.getLogger(NettyMessageClientHandler.class);

    private StringBuffer result = new StringBuffer();

    private NettyResponse<String> nettyResponse;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        result.setLength(0);

        this.nettyResponse = (NettyResponse<String>) ctx.channel().attr(NettyClient.NETTY_CLIENT_RESPONSE).get();

        FullHttpRequest request = (FullHttpRequest) ctx.channel().attr(NettyClient.NETTY_CLIENT_REQUEST).get();
        if(request == null){
            this.nettyResponse.setError(new SystemException("request init failed..."));
            return;
        }

        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

//        this.nettyResponse = (NettyResponse<String>) ctx.channel().attr(NettyClient.NETTY_CLIENT_RESPONSE).get();

        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            if(!HttpResponseStatus.OK.equals(response.status())){
                this.nettyResponse.setError(new SystemException("netty client no response"));
                return;
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            result.append(content.content().toString(CharsetUtil.UTF_8));
            if (content instanceof LastHttpContent) {
                this.nettyResponse.setSuccess(result.toString());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        this.nettyResponse.setError(cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().close();
    }
}
