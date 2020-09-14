package com.dounion.server.core.netty.client.handlers;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.helper.StringHelper;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.eum.NettyRequestTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;

@ChannelHandler.Sharable
public class NettyFileClientHandler extends AbstractNettyClientHandler<String> {


    private boolean reading = false;
    private String fileName;
    private File downloadFile = null;
    private FileOutputStream fos = null;
    private int successCode = 200;


    /**
     * 判断是否是文件下载返回
     *
     * @param ctx
     * @return
     */
    protected boolean isMatch(ChannelHandlerContext ctx) {
        NettyRequestTypeEnum eum =
                (NettyRequestTypeEnum) ctx.channel().attr(NettyClient.NETTY_CLIENT_REQUEST_TYPE).get();
        return NettyRequestTypeEnum.FILE.equals(eum);
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
        if (!isMatch(ctx)) {
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if (msg instanceof HttpResponse) {
                this.response = (HttpResponse) msg;
            }

            if (this.fileName == null) {
                this.fileName = StringHelper.getFileName(this.request.uri());
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
                if (successCode == 200) {
                    while (buffer.isReadable()) {
                        buffer.readBytes(dst);
                        fos.write(dst);
                    }

                    if (null != fos) {
                        fos.flush();
                    }
                }
            }

            if (!reading) {
                if (downloadFile != null && downloadFile.exists()) {
                    this.nettyResponse.setSuccess(downloadFile.getPath());
                } else {
                    this.nettyResponse.setError(new SystemException("file 【" + fileName + "】 download failed"));
                }
                if (null != fos) {
                    fos.flush();
                    fos.close();
                    downloadFile = null;
                    fos = null;
                }
                ctx.channel().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void setDownLoadFile() throws Exception {
        if (null == fos) {
            downloadFile = new File(Constant.PATH_DOWNLOAD + fileName);
            if (!downloadFile.getParentFile().exists()) {
                downloadFile.getParentFile().mkdirs();
            }
            if (!downloadFile.exists()) {
                downloadFile.createNewFile();
            }
            fos = new FileOutputStream(downloadFile);
        }
    }

}
