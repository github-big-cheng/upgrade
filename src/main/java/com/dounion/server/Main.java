package com.dounion.server;

import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.netty.NettyServer;
import com.dounion.server.core.request.HandlerMappingConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {


    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(BeanConfig.class);

        SpringApp.init(context);
        HandlerMappingConfig.initialization();

        ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);
        new NettyServer(serviceInfo).startUp();

    }


}
