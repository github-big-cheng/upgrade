package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.DeployTypeEnum;


@Deploy(deployType = DeployTypeEnum.FILE)
public class FileScript extends AbstractScript {


    @Override
    public String[] command() {
        return new String[]{
                os.getScriptCallMethod() + // sh or exe
                " file_deploy" + os.getScriptSuffix() // shell name
                + " " + params[0] // 待更新文件
                + " " + params[1] // 工作路径
                + " " + params[2] // 文件名称
        };
    }
}
