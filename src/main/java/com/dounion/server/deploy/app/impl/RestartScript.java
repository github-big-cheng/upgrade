package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.DeployTypeEnum;


@Deploy(deployType = DeployTypeEnum.RESTART)
public class RestartScript extends AbstractScript {

    @Override
    protected String[] command() {

        if(params==null || params.getAppInfo()==null){
            throw new BusinessException("RestartScript: please check params");
        }

        // 非 tomcat / main 不支持重启
        if(!DeployTypeEnum.TOMCAT.equals(params.getAppInfo().getDeployTypeEnum()) &&
                !DeployTypeEnum.MAIN.equals(params.getAppInfo().getDeployTypeEnum())){
            throw new SystemException("deploy type 【{}】 restart operation is unsupported",
                    params.getAppInfo().getDeployTypeEnum().toString());
        }

        return new String[]{
            os.getScriptCallMethod() // sh or exe
            + " restart" + os.getScriptSuffix() // shell name
            + " " + params.getAppInfo().getWorkPath() // 工作路径
            + " " + params.getAppInfo().getAppName() // 应用名称
            + " " + params.getAppInfo().getDeployTypeEnum().getDesc() // 部署类型
        };
    }

}
