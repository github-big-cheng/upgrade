package com.dounion.server.task;

import com.dounion.server.Main;
import com.dounion.server.core.base.AppInfo;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.DeployHandler;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.core.netty.server.NettyServer;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.dao.VersionInfoMapper;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.deploy.os.OperatingSystemFactory;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.eum.AppTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * 部署后台任务
 *      本地任务
 */
@Task(Constant.TASK_DEPLOY)
public class DeployTask extends BaseTask {

    @Autowired
    private ServiceInfo serviceInfo;

    @Autowired
    private VersionInfoMapper versionInfoMapper;

    @Override
    public String getTaskName() {
        return "部署后台任务";
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    protected void execute() throws Exception {

        // some logic about do upgrade
        if(CollectionUtils.isEmpty(super.params) ||
                super.getParams().get("versionId")==null){
            logger.error("【{}】 versionId is needed, deploy task will be exit", this);
            return;
        }

        Integer versionId = (Integer) super.params.get("versionId");
        VersionInfo versionInfo = versionInfoMapper.selectByPrimaryKey(versionId);
        if(versionInfo == null){
            logger.error("【{}】 version has been expired, deploy task will be exit", this);
            throw new BusinessException("version record not found");
        }

        super.setProgressTwentyFive(); // progress 25%

        // 获取本地应用
        AppInfo appInfo = serviceInfo.appInfoPickUp(versionInfo.getAppType());
        if(appInfo == null){
            logger.warn("【{}】 local service list is empty, deploy task exit", this);
            return;
        }

        // check version
        // 版本号等于或低于当前版本则不发布
        if(appInfo.getVersionNo().compareTo(versionInfo.getVersionNo()) >= 0){
            logger.warn("【{}】 Remote publish ! Current version is {}, deploy version is {}, deploy task will be exit",
                    this, appInfo.getVersionNo(), versionInfo.getVersionNo());
            return;
        }

        // deploy
        AbstractScript.ScriptParams scriptParams = new AbstractScript.ScriptParams();
        scriptParams.setAppInfo(appInfo);
        scriptParams.setVersionInfo(versionInfo);

        AbstractScript script = DeployHandler.getDeploy(appInfo.getDeployTypeEnum());
        script.setOs(OperatingSystemFactory.build());
        script.setParams(scriptParams);
        script.deploy();

        // upgrade 主程序退出
        if(AppTypeEnum.UPGRADE.equals(appInfo.getAppTypeEnum())){
            Main.setLastThread(Thread.currentThread());
            NettyServer.close(); // 解除端口占用
            logger.trace("NettyServer closed...");
        }

        super.setProgressNeelyComplete(); // progress 95%

        // 发布完成更新serviceInfo 信息
        appInfo.setVersionNo(versionInfo.getVersionNo());
        serviceInfo.toFile();

        super.setProgressComplete(); // progress 100%

        logger.trace("It has running to here!~~~~~~~~~~~~");


        // upgrade 主程序重新启动
        if(AppTypeEnum.UPGRADE.equals(appInfo.getAppTypeEnum())){
            logger.trace("Main function restart!~~~~~~~~~~~~");
            Main.main(null);
        }
    }
}
