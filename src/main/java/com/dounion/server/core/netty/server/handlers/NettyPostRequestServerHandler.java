package com.dounion.server.core.netty.server.handlers;

import com.alibaba.fastjson.JSON;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.request.MappingConfigHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@ChannelHandler.Sharable
public class NettyPostRequestServerHandler extends NettyHttpRequestServerHandler {

    private Logger logger = LoggerFactory.getLogger(NettyPostRequestServerHandler.class);

    private HttpPostRequestDecoder decoder;
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
    private StringBuffer message = new StringBuffer();

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        // on exit (in normal exit)
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
                    // 仅限post请求
                    this.request.method().equals(HttpMethod.POST) &&
                    // 是否是已注册的服务
                    MappingConfigHandler.isMapping(this.request.uri());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        message.setLength(0);
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
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            // url参数转换
            if(msg instanceof HttpRequest){
                super.convertUrlParams();
            }

            if(decoder != null){
                if(msg instanceof HttpContent){
                    HttpContent chunk = (HttpContent) msg;
                    writeJson(chunk);
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
        } catch (Exception e) {
            throw e;
        } finally {
            ReferenceCountUtil.release(msg);
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
                            String fileName = fileUpload.getFilename();
                            if(StringUtils.contains(fileName, File.separator)){
                                fileName = StringUtils.substring(fileName, fileName.lastIndexOf(File.separator)+1);
                            }
                            final File file = new File(Constant.PATH_DOWNLOAD + fileName);
                            if(file.exists()){
                                file.delete();
                            }
                            if(!file.getParentFile().exists()){
                                file.getParentFile().mkdirs();
                            }
                            fileUpload.renameTo(file); // enable to move into another
                            decoder.removeHttpDataFromClean(fileUpload); //remove
                            logger.debug("upload file: {}", file);
                            params.put(fileUpload.getName(), file);
                        }
                    }
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
            //ignore. that's fine.
        }


    }

    private void writeJson(HttpContent content){
        try {
//            String contentType = this.request.headers().get(HttpHeaderNames.CONTENT_TYPE);
//            if(!StringUtils.contains(contentType, Constant.CONTENT_TYPE_JSON)){
//                return;
//            }
            String json = content.content().toString(CharsetUtil.UTF_8);
            if(StringUtils.isBlank(json) ||
                    !(StringUtils.startsWith(json,"{") && StringUtils.endsWith(json, "}")) ||
                            (StringUtils.startsWith(json, "[") && StringUtils.endsWith(json, "]"))){
                return;
            }

            Map<String, Object> params = JSON.parseObject(json, Map.class);
            this.params.putAll(params);
        } catch (Exception e) {
            //ignore. that's fine.
//            logger.error("parse error:{}", e);
        }
    }

}
