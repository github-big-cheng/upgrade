package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.DeployTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;


@Deploy(deployType = DeployTypeEnum.MYSQL)
public class SqlScript extends AbstractScript {

    @Autowired
    private ServiceInfo serviceInfo;

    @Override
    public String[] command() {

        if(params==null || params.getVersionInfo()==null){
            throw new BusinessException("SqlScript: please check params");
        }

        return new String[]{
                os.getScriptCallMethod() + // sh or exe
                " sql_deploy" + os.getScriptSuffix() // shell name
                + " " + serviceInfo.getCode() // 库点代码
                + " " + params.getVersionInfo().getFilePath() // sql.zip 路径
                + " " + params.getAppInfo().getUsername() // 数据库用户名
                + " " + params.getAppInfo().getPassword() // 数据库密码
        };
    }
}
