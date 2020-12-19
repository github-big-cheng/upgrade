package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.DeployTypeEnum;


@Deploy(deployType = DeployTypeEnum.MAIN)
public class MainScript extends AbstractScript {

    @Override
    public String[] command() {

        if(params==null || params.getVersionInfo()==null || params.getAppInfo()==null){
            throw new BusinessException("MainScript: please check params");
        }

        ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);

        return new String[]{
                os.getScriptCallMethod() + // sh or exe
                " main_deploy" + os.getScriptSuffix() // shell name
                + " " + params.getVersionInfo().getFilePath() // 待更新文件
                + " " + params.getAppInfo().getWorkPath() // 工作路径
                + " " + params.getAppInfo().getAppName() // 文件名称
                + " " + serviceInfo.getBackUpPath() // 备份路径
        };
    }
}
