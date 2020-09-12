package com.dounion.server.core.netty.server;

import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.netty.client.NettyClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * Netty服务端
 *      提供web/upload/download服务
 */
public class NettyServer {

    private final static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private NettyServer(){
    }

    private ServerBootstrap bootstrap;
    private ChannelFuture future;


    private static NettyServer nettyServer = new NettyServer();

    public static NettyServer getInstance(){
        return nettyServer;
    }


    public static void startUp(){

        ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);
        Assert.notNull(serviceInfo, "serviceInfo didn't init");
        Assert.notNull(serviceInfo.getPort(), "port must be define");


        EventLoopGroup bossGroup = new NioEventLoopGroup(8);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            nettyServer.bootstrap = new ServerBootstrap();
            nettyServer.bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer())
                ;

            final String localIp = serviceInfo.getLocalIp();
            final int port = serviceInfo.getPort();

            nettyServer.future = nettyServer.bootstrap.bind(serviceInfo.getPort());
            nettyServer.future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    logger.info("|--------------------------------------------------------------------|");
                    logger.info("|++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|");
                    logger.info("|+ Server start up successfully, port bind with 【{}】             +|", port);
                    logger.info("|+ Open your web browser and navigate to http://{}:{}/list +|", localIp, port);
                    logger.info("|++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++|");
                    logger.info("|--------------------------------------------------------------------|");
                }
            });

            nettyServer.future.channel().closeFuture().syncUninterruptibly();

        } catch (Exception e) {
            logger.error("Netty Server error:{}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void restart(){
        if(nettyServer.future != null){
            nettyServer.future.channel().close();
        }
        startUp();
    }

}
