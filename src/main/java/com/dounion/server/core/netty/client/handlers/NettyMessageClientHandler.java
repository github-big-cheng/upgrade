package com.dounion.server.core.netty.client.handlers;

import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.netty.client.NettyResponse;
import com.dounion.server.eum.NettyRequestTypeEnum;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class NettyMessageClientHandler extends AbstractNettyClientHandler<String> {

    private StringBuffer result = new StringBuffer();


    @Override
    protected boolean isMatch(ChannelHandlerContext ctx) {
        NettyRequestTypeEnum eum =
                (NettyRequestTypeEnum) ctx.channel().attr(NettyClient.NETTY_CLIENT_REQUEST_TYPE).get();
        return NettyRequestTypeEnum.MESSAGE.equals(eum);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(this.nettyResponse == null){
            this.nettyResponse =
                    (NettyResponse<String>) ctx.channel().attr(NettyClient.NETTY_CLIENT_RESPONSE).get();
        }

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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().close();
    }
}
