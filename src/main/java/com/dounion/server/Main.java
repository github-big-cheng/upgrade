package com.dounion.server;

import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.DeployHandler;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.netty.server.NettyServer;
import com.dounion.server.core.request.MappingConfigHandler;
import com.dounion.server.core.task.LockHandler;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.deploy.os.OperatingSystem;
import com.dounion.server.deploy.os.OperatingSystemFactory;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private static boolean RESTART = false;

    public static void setRestart(boolean flag){
        RESTART = flag;
    }

    public static void main(String[] args) {

        logger.trace("new version 1 of upgrade");
        logger.trace("args : {}", Arrays.toString(args));

        try {
            AnnotationConfigApplicationContext context =
                    new AnnotationConfigApplicationContext(BeanConfig.class);
            // 初始化Spring容器
            SpringApp.init(context);
            // 初始化路由表
            MappingConfigHandler.initialization();
            // 初始化部署任务处理器
            DeployHandler.initialization();
            // 初始化后台任务管理器
            TaskHandler.initialization();


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

            // when NettyServer channel closed, check is it restart
            if(RESTART){
                // call shell to restart
                logger.trace("do restart...");
                OperatingSystem system = OperatingSystemFactory.build();
                // run.sh with parameter will not shell stop.sh, this thread will exit at finally System.exit(0)
                String[] cmd = (String[]) ArrayUtils.addAll(system.getDefaultEnvironmentCmd(), new String[]{"sh run.sh 1"});
                DeployHandler.execute(Constant.PATH_WORK, cmd);
            }

        } catch (Exception e) {
            logger.error("server start up failed... {}", e);
        } finally {
            close();

            logger.trace("do exit...");
            System.exit(0);
        }

    }


    /**
     * 服务关闭
     */
    public static void close(){
        // all has down, shut down netty server
        logger.trace("NettyServer.close() running...");
        NettyServer.close();
        // all has down, shut down executors
        logger.trace("TaskHandler.shutdown() running...");
        TaskHandler.shutdown();
        // all has down, shut down locks
        logger.trace("LockHandler.shutdown() running...");
        LockHandler.shutdown();
    }


    /**
     * 重启
     */
    public static void restart(){

        logger.trace("NettyServer closed...");
        NettyServer.close(); // 解除端口占用

        RESTART = true;
    }

}
