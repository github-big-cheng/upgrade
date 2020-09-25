package com.dounion.server;

import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.DeployHandler;
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
            // 初始化部署任务处理器
            DeployHandler.initialization();


            // 运行指定后台任务
            ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);
            // master
            if(!serviceInfo.getMasterBlur()) {
                // 订阅更新服务 -- 10秒后
                TaskHandler.callTask(Constant.TASK_SUBSCRIBE, 10 * 1000);
                // 分发下载路由注册服务 -- 30秒后
                TaskHandler.callTask(Constant.TASK_ROUTE, 30 * 1000);
            }
            // 自动发布任务 -- 60秒后
            TaskHandler.callTask(Constant.TASK_PUBLISH_AUTO, 60 * 1000);


            // 启动服务端
            NettyServer.startUp();
            // Netty has blocked here, no code should exists after this line

        } catch (Exception e) {
            logger.error("server start up failed... {}", e);
        } finally {
            logger.info("upgrade server will exit in 10 seconds later...");
            try {
                // some other progress is running, waiting 5 seconds for them
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }


}
