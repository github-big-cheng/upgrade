package com.dounion.server.core.netty.client.handlers;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.netty.client.NettyClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

@ChannelHandler.Sharable
public class NettyFileClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Logger logger = LoggerFactory.getLogger(NettyFileClientHandler.class);

    private HttpResponse response;
    private boolean reading = false;
    private String fileName;
    private File downloadFile = null;
    private FileOutputStream fos = null;
    private int successCode = 200;


    /**
     * 判断是否是文件下载返回
     * @param msg
     * @return
     */
    private boolean isMatch(HttpObject msg){
        String contentType = null;
        if(msg instanceof HttpResponse){
            this.response = (HttpResponse) msg;
            contentType = response.headers().get(HttpHeaderNames.CONTENT_TYPE);
        }

        return this.response!=null &&
                    StringUtils.contains(contentType, Constant.CONTENT_TYPE_FILE);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        fileName = null;
        downloadFile = null;
        fos = null;
        ctx.channel().close();
    }

    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        // 判断是否处理
        if(!isMatch(msg)){
            ctx.fireChannelRead(msg);
            return;
        }

        if(this.fileName == null){
            this.fileName = (String) ctx.channel().attr(NettyClient.FILE_NAME).get();
        }

        if (msg instanceof HttpResponse) {
            successCode = response.status().code();
            if (successCode == 200) {
                setDownLoadFile();
                reading = true;
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent chunk = (HttpContent) msg;
            if (chunk instanceof LastHttpContent) {
                reading = false;
            }

            ByteBuf buffer = chunk.content();
            byte[] dst = new byte[buffer.readableBytes()];
            if(successCode == 200) {
                while(buffer.isReadable()) {
                    buffer.readBytes(dst);
                    fos.write(dst);
                }

                if (null != fos) {
                    fos.flush();
                }
            }
        }

        if (!reading) {
            if (null != fos) {
                fos.flush();
                fos.close();
                downloadFile = null;
                fos = null;
            }
            ctx.channel().close();
        }
    }

    private void setDownLoadFile() throws Exception {
        if(null == fos) {
            downloadFile = new File(Constant.PATH_DOWNLOAD + fileName);
            if(!downloadFile.getParentFile().exists()){
                downloadFile.getParentFile().mkdirs();
            }
            if(!downloadFile.exists()) {
                downloadFile.createNewFile();
            }
            fos = new FileOutputStream(downloadFile);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("netty client error:{}", cause);
        ctx.channel().close();
    }


}
