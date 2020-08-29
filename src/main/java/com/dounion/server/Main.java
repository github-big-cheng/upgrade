package com.dounion.server;

import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.netty.server.NettyServer;
import com.dounion.server.core.request.HandlerMappingConfig;
import com.dounion.server.task.SubscribeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            AnnotationConfigApplicationContext context =
                    new AnnotationConfigApplicationContext(BeanConfig.class);

            SpringApp.init(context);
            HandlerMappingConfig.initialization();

            // 启动服务端
            ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);
            new NettyServer(serviceInfo).startUp();

            // 服务注册
            new SubscribeTask().start();
        } catch (Exception e) {
            logger.error("server start up failed... {}", e);
        }
    }


}
