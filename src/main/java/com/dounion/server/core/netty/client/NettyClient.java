package com.dounion.server.core.netty.client;


import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.eum.NettyRequestTypeEnum;
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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 简易netty客户端
 * 需请求的主机一般只有对应的master主机
 */
public class NettyClient implements Closeable {

    private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    // 工作组
    final static EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(2);
    public final static AttributeKey NETTY_CLIENT_REQUEST_TYPE = AttributeKey.newInstance("NETTY_CLIENT_REQUEST_TYPE");
    public final static AttributeKey NETTY_CLIENT_REQUEST = AttributeKey.newInstance("NETTY_CLIENT_REQUEST");
    public final static AttributeKey NETTY_CLIENT_RESPONSE = AttributeKey.newInstance("NETTY_CLIENT_RESPONSE");


    private static Object LOCK = new Object();
    private static NettyClient masterInstance = null;

    // private constructor
    private NettyClient(String ip, int port, boolean isMaster) {

        this.isMaster = isMaster;
        this.ip = ip;
        this.port = port;

        bootstrap = new Bootstrap();
        bootstrap.group(WORKER_GROUP)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true) // 服务端暂未处理 keepAlive
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new NettyClientInitializer());

        this.connect(ip, port);
    }

    public void connect(String ip, int port) {
        this.future = bootstrap.connect(ip, port);
    }

    private String ip;
    private int port;
    private boolean isMaster;
    private Bootstrap bootstrap;
    private ChannelFuture future;


    @Override
    public String toString() {
        return "[ ip:" + this.ip + ", port:" + this.port + ", isMaster:" + this.isMaster + "]";
    }

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
    public static NettyClient getInstance(String url) {
        try {
            URI uri = new URI(url);
            int port = uri.getPort()==-1 ? (StringUtils.equals(uri.getScheme(), "https") ? 443 : 80) : uri.getPort();
            return new NettyClient(uri.getHost(), port, false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取NettyClient
     * @return
     */
    public static NettyClient getMasterInstance() {

        if(masterInstance != null){
            // 已完成，重新连接
            if(masterInstance.future.isSuccess()){
                masterInstance.future.channel().close();
                masterInstance.connect(masterInstance.ip, masterInstance.port);
            }

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

    public void close(){
        if(this.future != null){
            this.future.channel().close();
        }
    }


    /**
     * 文件下载
     *      默认重定向3次
     * @param url
     * @return
     */
    public String fileDownload(String url) {
        return fileDownload(url, 0, 3);
    }


    /**
     * 文件下载
     * @param url
     * @param inx 当前重定向次数
     * @param maxRedirectCount
     * @return
     */
    public String fileDownload(String url, int inx, int maxRedirectCount) {

        try {
            logger.debug("【{}】 fileDownload execute...url:【{}】", this, url);

            Channel channel = this.future.channel();
            // 设置请求类型
            channel.attr(NETTY_CLIENT_REQUEST_TYPE).set(NettyRequestTypeEnum.FILE);
            // 设置请求对象
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url);
            channel.attr(NETTY_CLIENT_REQUEST).set(request);
            // 设置响应处理 文件放宽等待时间 默认20分钟
            Long maxDownloadTime = ConfigurationHelper.getLong(Constant.CONF_DOWNLOAD_MAX_WAIT, 20 * 60 * 1000l);
            NettyResponse<String> response = new NettyResponse<>(maxDownloadTime);
            channel.attr(NETTY_CLIENT_RESPONSE).set(response);

            String fileName = response.get();
            logger.debug("NettyResponse:[status={}, v={}, throwable={}]",
                    response.getStatus(), response.getV(), response.getThrowable());
            if(response.getStatus()==302){
                // 重定向次数过多，返回空
                if(inx >= maxRedirectCount){
                    logger.error("redirect to many times:{}", url);
                    return null;
                }
                // 重定向处理, 这里的fileName是url
                NettyClient client = NettyClient.getInstance(fileName);
                logger.debug("do redirect, client is:{}", client);
                fileName = client.fileDownload(url, inx++, maxRedirectCount);
            }

            return fileName;
        } finally {
            if(!masterInstance.equals(this)){
                this.close();
            }
        }
    }


    public String doHttpRequest(Map<Object, String> params){

        try {
            String url = params.get(Constant.HTTP_URL);
            Assert.notNull(url, "url can not be null,please check it");

            logger.debug("【{}】 doHttpRequest execute...url:【{}】,params【{}】", this, url, params);

            // 请求方式
            HttpMethod httpMethod = HttpMethod.valueOf(params.get(Constant.HTTP_METHOD));
            if(httpMethod == null){
                httpMethod = HttpMethod.GET;
            }

            // 请求报文
            String message = params.get(Constant.HTTP_MESSAGE);
            if(StringUtils.isBlank(message)){
                message = "";
            }
            ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);

            // 创建request对象
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, url, buf);
            HttpUtil.setContentLength(request, request.content().readableBytes());

            // 请求头其他参数 content/cookie 等
            for(Object key : params.keySet()){
                if(key instanceof AsciiString){
                    request.headers().set((AsciiString) key, params.get(key));
                }
            }

            Channel channel = this.future.channel();


            NettyResponse<String> response = new NettyResponse<>();
            channel.attr(NETTY_CLIENT_REQUEST_TYPE).set(NettyRequestTypeEnum.MESSAGE);
            channel.attr(NETTY_CLIENT_RESPONSE).set(response);
            channel.attr(NETTY_CLIENT_REQUEST).set(request);

            // 阻塞获取结果
            String result = response.get();
            if(response.getStatus() != 200){
                logger.error("NettyClient.doHttpRequest fail:[status:{}], {}", response.getStatus(), response.getThrowable());
            }
            return result;
        } finally {
            if(!this.equals(masterInstance)){
                this.close();
            }
        }
    }

}
