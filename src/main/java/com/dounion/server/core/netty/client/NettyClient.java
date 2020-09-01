package com.dounion.server.core.netty.client;


import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.helper.StringHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 简易netty客户端
 * 需请求的主机一般只有对应的master主机
 */
public class NettyClient {

    private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    // 工作组
    final static EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(2);
    public final static AttributeKey FILE_NAME = AttributeKey.newInstance("NETTY_CLIENT_FILE_NAME");
    public final static AttributeKey NETTY_CLIENT_REQUEST = AttributeKey.newInstance("NETTY_CLIENT_REQUEST");
    public final static AttributeKey NETTY_CLIENT_RESPONSE = AttributeKey.newInstance("NETTY_CLIENT_RESPONSE");


    private static Object LOCK = new Object();
    private static NettyClient masterInstance = null;

    // private constructor
    private NettyClient(String ip, int port, boolean isMaster) {

        this.isMaster = isMaster;

        bootstrap = new Bootstrap();
        bootstrap.group(WORKER_GROUP)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true) // 服务端暂未处理 keepAlive
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new NettyClientInitializer());

        this.future = bootstrap.connect(ip, port);
    }

    private boolean isMaster;
    private Bootstrap bootstrap;
    private ChannelFuture future;

    /**
     * 获取NettyClient
     * @return
     */
    public static NettyClient getInstance(String ip, int port) {
        return new NettyClient(ip, port, false);
    }

    /**
     * 获取NettyClient
     * @return
     */
    public static NettyClient getMasterInstance() {

        if(masterInstance != null){
            return masterInstance;
        }

        synchronized (LOCK) {
            // double-check
            if(masterInstance != null){
                return masterInstance;
            }

            ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);
            Assert.notNull(serviceInfo.getMasterIp(), "master ip must be config");
            Assert.notNull(serviceInfo.getMasterPort(), "master port must be config");
            masterInstance = new NettyClient(serviceInfo.getMasterIp(), serviceInfo.getMasterPort(), true);
        }
        return masterInstance;
    }


    /**
     * 构建Get请求参数
     * @param url
     * @return
     */
    public static Map<String, String> buildGetMap(String url){
        Map<String, String> head = new HashMap<>();
        head.put(Constant.HTTP_METHOD, Constant.HTTP_METHOD_GET);
        head.put(Constant.HTTP_URL, url);
        return head;
    }

    /**
     * 构建Get请求参数
     *      支持添加 HttpHeaderNames.COOKIE
     *              HttpHeaderNames.CONTENT_TYPE
     *              等
     * @param url
     * @param message
     * @return
     */
    public static Map<Object, String> buildPostMap(String url, String message){
        Map<Object, String> head = new HashMap<>();
        head.put(Constant.HTTP_METHOD, Constant.HTTP_METHOD_POST);
        head.put(Constant.HTTP_URL, url);
        head.put(Constant.HTTP_MESSAGE, message);
        return head;
    }


    public void fileDownload(String url) {
        try {
            String fileName = StringHelper.getFileName(url);

            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url);
            Channel channel = this.future.channel();
            channel.attr(FILE_NAME).set(fileName);
            channel.writeAndFlush(request);
            this.future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public String doHttpRequest(Map<Object, String> params){

        String url = params.get(Constant.HTTP_URL);
        Assert.notNull(url, "url can not be null,please check it");

        // 请求方式
        HttpMethod httpMethod = HttpMethod.valueOf(params.get(Constant.HTTP_METHOD));
        if(httpMethod == null){
            httpMethod = HttpMethod.GET;
        }

        // 请求报文
        String message = params.get(Constant.HTTP_MESSAGE);
        if(org.apache.commons.lang.StringUtils.isBlank(message)){
            message = "";
        }
        ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);

        // 创建request对象
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, url, buf);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(request.content().readableBytes()));

        // 请求头其他参数 content/cookie 等
        for(Object key : params.keySet()){
            if(key instanceof AsciiString){
                request.headers().set((AsciiString) key, params.get(key));
            }
        }

        NettyResponse<String> response = new NettyResponse<>();
        this.future.channel().attr(NETTY_CLIENT_RESPONSE).set(response);
        this.future.channel().attr(NETTY_CLIENT_REQUEST).set(request);

        // 阻塞获取结果
        String result = response.get();
        if(response.getStatus() != 200){
            logger.error("NettyClient.doHttpRequest fail:[status:{}], {}", response.getStatus(), response.getThrowable());
        }
        return result;
    }

}
