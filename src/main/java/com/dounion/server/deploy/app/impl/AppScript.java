package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.base.Constant;
import com.dounion.server.deploy.app.AbstractScript;
import org.springframework.stereotype.Component;

@Component("app")
public class AppScript extends AbstractScript {

    @Override
    public String getWorkDirectory() {
        return Constant.PATH_SCRIPT + os.getScriptPackage();
    }

    @Override
    public String[] command() {
        return new String[]{
                os.getScriptCallMethod() + // sh or exe
                " tomcat_deploy" + os.getScriptSuffix() // shell name
                + " " + params[0] // 待更新文件
                + " " + params[1] // 工作路径
                + " " + params[2] // 文件名称
        };
    }
}
