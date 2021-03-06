package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.DeployTypeEnum;


@Deploy(deployType = DeployTypeEnum.FILE)
public class FileScript extends AbstractScript {


    @Override
    public String[] command() {

        if(params==null || params.getVersionInfo()==null || params.getAppInfo()==null){
            throw new BusinessException("FileScript: please check params");
        }

        return new String[]{
                os.getScriptCallMethod() + // sh or exe
                " file_deploy" + os.getScriptSuffix() // shell name
                + " " + params.getVersionInfo().getFilePath() // 待更新文件
                + " " + params.getAppInfo().getWorkPath() // 工作路径
                + " " + params.getVersionInfo().getFileName() // 应用名称
        };
    }
}
