package com.dounion.server.core.netty.server;

import com.dounion.server.core.base.ServiceInfo;
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

    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ServiceInfo serviceInfo;

    public NettyServer(ServiceInfo serviceInfo) {
        Assert.notNull(serviceInfo.getPort(), "port must be define");
        this.serviceInfo = serviceInfo;
    }

    public void startUp(){

        EventLoopGroup bossGroup = new NioEventLoopGroup(8);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyServerInitializer())
                ;

            final String localIp = this.serviceInfo.getLocalIp();
            final int port = this.serviceInfo.getPort();

            ChannelFuture f = bootstrap.bind(this.serviceInfo.getPort());
            f.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    logger.info("==========================================================");
                    logger.info("Server start up successfully, port bind with 【{}】", port);
                    logger.info("Open your web browser and navigate to http://{}:{}/list", localIp, port);
                    logger.info("==========================================================");
                }
            });

            f.channel().closeFuture().sync();

        } catch (Exception e) {
            logger.error("Netty Server error:{}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
