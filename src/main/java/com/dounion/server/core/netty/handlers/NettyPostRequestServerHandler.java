package com.dounion.server.core.netty.handlers;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.helper.StringHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

@ChannelHandler.Sharable
public class NettyPostRequestServerHandler extends NettyHttpRequestServerHandler {

    private Logger logger = LoggerFactory.getLogger(NettyPostRequestServerHandler.class);

    @Override
    protected Boolean isMatch(Object msg) {
        if(msg instanceof HttpRequest){
            this.request = (HttpRequest) msg;
        }
        return this.request!=null &&
                    this.request.method().equals(HttpMethod.POST) &&
                    !StringHelper.isStaticRequest(request.uri());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(!this.isMatch(msg)){
            logger.debug("NettyPostRequestServerHandler not match..{}", msg);
            ctx.fireChannelRead(msg);
            return;
        }

        if(msg instanceof HttpRequest){
            super.convertUrlParams();
        }
        if(decoder != null){
            if(msg instanceof HttpContent){
                HttpContent chunk = (HttpContent) msg;
                decoder.offer(chunk);
                if (chunk instanceof LastHttpContent) {
                    readHttpDataChunkByChunk();
                    decoder.destroy();
                    decoder = null;
                    writeResponse(ctx);
                    ReferenceCountUtil.release(msg);
                }
            }
        } else {
            if(msg instanceof LastHttpContent){
                writeResponse(ctx);
            }
        }

    }


    private void readHttpDataChunkByChunk() throws IOException {

        List<InterfaceHttpData> httpDatas = decoder.getBodyHttpDatas();
        for(InterfaceHttpData data : httpDatas){
            if (data != null) {
                // check if current HttpData is a FileUpload and previously set as partial
                if (partialContent == data) {
                    logger.info(" 100% (FinalSize: " + partialContent.length() + ")");
                    partialContent = null;
                }
                // new value
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) data;
                    String value = attribute.getValue();
                    if(params.containsKey(attribute.getName())){
                        value = params.get(attribute.getName()) + "," + value;
                    }
                    params.put(attribute.getName(), value);
                } else {
                    // data.getHttpDataType().name() + ": " + data
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                        FileUpload fileUpload = (FileUpload) data;
                        final File temp = new File(Constant.TEMP_PATH + fileUpload.getFilename());
                        try (FileChannel inputChannel = new FileInputStream(fileUpload.getFile()).getChannel();
                             FileChannel outputChannel = new FileOutputStream(temp).getChannel()) {
                            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                        }
                        if (fileUpload.isCompleted()) {
                            params.put(fileUpload.getName(), temp);
                        }
                    }
                }
            }

        }

        // Check partial decoding for a FileUpload
        InterfaceHttpData data = decoder.currentPartialHttpData();
        if (data != null) {
            StringBuilder builder = new StringBuilder();
            if (partialContent == null) {
                partialContent = (HttpData) data;
                if (partialContent instanceof FileUpload) {
                    builder.append("Start FileUpload: ")
                            .append(((FileUpload) partialContent).getFilename()).append(" ");
                } else {
                    builder.append("Start Attribute: ")
                            .append(partialContent.getName()).append(" ");
                }
                builder.append("(DefinedSize: ").append(partialContent.definedLength()).append(")");
            }
            if (partialContent.definedLength() > 0) {
                builder.append(" ").append(partialContent.length() * 100 / partialContent.definedLength())
                        .append("% ");
                logger.info(builder.toString());
            } else {
                builder.append(" ").append(partialContent.length()).append(" ");
                logger.info(builder.toString());
            }
        }
    }

}
