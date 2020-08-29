package com.dounion.server.core.netty.handlers;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.helper.StringHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@ChannelHandler.Sharable
public class NettyPostRequestServerHandler extends NettyHttpRequestServerHandler {

    private Logger logger = LoggerFactory.getLogger(NettyPostRequestServerHandler.class);

    private HttpPostRequestDecoder decoder;
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        // on exit (in normal
        // exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
        // exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }


    @Override
    protected Boolean isMatch(Object msg) {
        if(msg instanceof HttpRequest){
            this.request = (HttpRequest) msg;
            if (this.request.method().equals(HttpMethod.POST)) {
                this.decoder = new HttpPostRequestDecoder(this.factory, this.request);
                this.decoder.setDiscardThreshold(0);
            }
        }
        return this.request!=null &&
                    this.request.method().equals(HttpMethod.POST) &&
                    !StringHelper.isStaticRequest(request.uri());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            // 清理文件
            decoder.cleanFiles();
        }
    }


    @Override
    protected void reset(){
        super.reset();
        if(this.decoder != null){
            // 销毁decoder
            decoder.destroy();
            decoder = null;
        }
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(!this.isMatch(msg)){
//            logger.debug("NettyPostRequestServerHandler not match..");
            ctx.fireChannelRead(msg);
            return;
        }

        // url参数转换
        if(msg instanceof HttpRequest){
            super.convertUrlParams();
        }

        if(decoder != null){
            if(msg instanceof HttpContent){
                HttpContent chunk = (HttpContent) msg;
                decoder.offer(chunk);
                writeChunk();
                if (chunk instanceof LastHttpContent) {
                    writeResponse(ctx);
                }
            }
        } else {
            if(msg instanceof LastHttpContent){
                writeResponse(ctx);
                return;
            }
        }

    }


    private void writeChunk() throws IOException {

        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    // new value
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) data;
                        String value = attribute.getValue();
                        if (params.containsKey(attribute.getName())) {
                            value = params.get(attribute.getName()) + "," + value;
                        }
                        params.put(attribute.getName(), value);
                    }
                    if (InterfaceHttpData.HttpDataType.FileUpload == data.getHttpDataType()) {
                        FileUpload fileUpload = (FileUpload) data;
                        if (fileUpload.isCompleted()) {
                            final File file = new File(Constant.DOWNLOAD_PATH + fileUpload.getFilename());
                            if(file.exists()){
                                file.delete();
                            }
                            fileUpload.renameTo(file); // enable to move into another
                            decoder.removeHttpDataFromClean(fileUpload); //remove
                            logger.debug("upload file: {}", fileUpload.getFile());
                            params.put(fileUpload.getName(), fileUpload.getFile());
                        }
                    }
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
            //ignore. that's fine.
        }
    }

}
