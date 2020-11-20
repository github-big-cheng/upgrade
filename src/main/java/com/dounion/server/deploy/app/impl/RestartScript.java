package com.dounion.server.deploy.app.impl;

import com.dounion.server.Main;
import com.dounion.server.core.base.AppInfo;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.eum.AppTypeEnum;
import com.dounion.server.eum.DeployTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;


@Deploy(deployType = DeployTypeEnum.RESTART)
public class RestartScript extends AbstractScript {

    @Autowired
    protected ServiceInfo serviceInfo;


    @Override
    protected String[] command() {

        if(params==null || params.getAppInfo()==null){
            throw new BusinessException("RestartScript: please check params");
        }

        AppInfo realAppInfo = getRealAppInfo(params.getAppInfo());
        logger.debug("real app info is {}", realAppInfo);

        // 非 tomcat / main 不支持重启
        if(!DeployTypeEnum.TOMCAT.equals(realAppInfo.getDeployTypeEnum()) &&
                !DeployTypeEnum.MAIN.equals(realAppInfo.getDeployTypeEnum()) &&
                !DeployTypeEnum.SPRING_BOOT.equals(realAppInfo.getDeployTypeEnum())
            ){
            throw new SystemException("deploy type 【{}】 restart operation is unsupported",
                    realAppInfo.getDeployTypeEnum().toString());
        }

        return new String[]{
            os.getScriptCallMethod() // sh or exe
            + " restart" + os.getScriptSuffix() // shell name
            + " " + realAppInfo.getWorkPath() // 工作路径
            + " " + realAppInfo.getAppName() // 应用名称
            + " " + realAppInfo.getDeployTypeEnum().getDesc() // 部署类型
        };
    }


    private AppInfo getRealAppInfo(AppInfo appInfo){
        if(appInfo == null){
            return null;
        }

        AppTypeEnum appTypeEnum = appInfo.getAppTypeEnum();
        if(appTypeEnum == null){
            return null;
        }
        if(appTypeEnum.getParent() == null){
            return appInfo;
        }

        return getRealAppInfo(serviceInfo.appInfoPickUp(appTypeEnum.getParent().getCode()));
    }


    @Override
    public void deploy() {
        AppInfo realAppInfo = getRealAppInfo(params.getAppInfo());
        if(AppTypeEnum.UPGRADE.equals(realAppInfo.getAppTypeEnum())){
            new Thread(){
                @Override
                public void run() {
                    try {
                        // 3秒后重启，方便结果返回
                        Thread.sleep(3 * 1000);

                        Main.restart();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return;
        }

        super.deploy();
    }
}
