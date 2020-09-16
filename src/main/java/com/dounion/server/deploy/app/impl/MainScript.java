package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.DeployTypeEnum;
import org.apache.commons.lang.StringUtils;


@Deploy(deployType = DeployTypeEnum.MAIN)
public class MainScript extends AbstractScript {

    @Override
    public String[] command() {
        return new String[]{
                os.getScriptCallMethod() + // sh or exe
                " main_deploy" + os.getScriptSuffix() // shell name
                + " " + params[0] // 待更新文件
                + " " + params[1] // 工作路径
                + " " + StringUtils.substring(params[2], 0, params[2].indexOf(".")) // 文件名称
        };
    }
}
