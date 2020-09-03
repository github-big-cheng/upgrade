package com.dounion.server;

import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.netty.server.NettyServer;
import com.dounion.server.core.request.MappingConfigHandler;
import com.dounion.server.core.task.TaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            AnnotationConfigApplicationContext context =
                    new AnnotationConfigApplicationContext(BeanConfig.class);

            // 初始化Spring容器
            SpringApp.init(context);
            // 初始化路由表
            MappingConfigHandler.initialization();

            ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);

            // 订阅更新服务
            TaskHandler.callTask(Constant.TASK_SUBSCRIBE, 5000);
            if(serviceInfo.getStandByBlur()){
                // 分发下载路由注册服务
                TaskHandler.callTask(Constant.TASK_DOWNLOAD_ROUTE, 10000);
            }


            // 启动服务端
            new NettyServer(serviceInfo).startUp();


        } catch (Exception e) {
            logger.error("server start up failed... {}", e);
        }
    }


}
