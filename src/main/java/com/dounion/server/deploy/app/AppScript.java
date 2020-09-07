package com.dounion.server.deploy.app;

import com.dounion.server.core.base.Constant;
import org.springframework.stereotype.Component;

@Component("app")
public class AppScript extends AbstractScript {

    @Override
    public String[] command() {
        return new String[]{
                "sh", // 调用方式
                Constant.PATH_SCRIPT + "tomcat_deploy.sh", // 脚本
                params[0], // 工作路径
                params[1] // 文件名称
        };
    }
}
