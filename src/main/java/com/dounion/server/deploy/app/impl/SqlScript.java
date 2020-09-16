package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.DeployTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;


@Deploy(deployType = DeployTypeEnum.MYSQL)
public class SqlScript extends AbstractScript {

    @Autowired
    private ServiceInfo serviceInfo;

    @Override
    public String[] command() {
        return new String[]{
                os.getScriptCallMethod() + // sh or exe
                " sql_deploy" + os.getScriptSuffix() // shell name
                + " " + serviceInfo.getCode() // 库点代码
                + " " + params[0] // sql.zip 路径
        };
    }
}
