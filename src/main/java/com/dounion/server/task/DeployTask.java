package com.dounion.server.task;

import com.alibaba.fastjson.JSON;
import com.dounion.server.core.base.AppInfo;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.deploy.DeployHandler;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.dao.VersionInfoMapper;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.deploy.os.OperatingSystemFactory;
import com.dounion.server.entity.VersionInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
    protected void execute() throws Exception {

        // some logic about do upgrade
        if(CollectionUtils.isEmpty(super.params) ||
                super.getParams().get("versionId")==null){
            logger.error("【{}】 versionId is needed, deploy task will be exit", this);
            return;
        }

        Integer versionId = (Integer) super.params.get("versionId");
        VersionInfo versionInfo = versionInfoMapper.selectByPrimaryKey(versionId);
        if(versionId == null){
            logger.error("【{}】 version has been expired, deploy task will be exit", this);
            return;
        }

        // 获取本地服务列表
        List<AppInfo> localServices = serviceInfo.getLocalServiceList();
        if(CollectionUtils.isEmpty(localServices)){
            logger.info("【{}】 local service list is empty, deploy task exit", this);
            return;
        }

        super.setProgressTwentyFive(); // progress 25%

        // called scripts here
        for(AppInfo appInfo : localServices){

            // find appType and check is it needed to deploy
            if(!StringUtils.equals(appInfo.getAppType(), versionInfo.getAppType())){
                continue;
            }

            // check version
            if(appInfo.getVersionNo().compareTo(versionInfo.getVersionNo()) > 0){
                logger.debug("【{}】 current version is {}, deploy version is {}, deploy task will be exit",
                        this, appInfo.getVersionNo(), versionInfo.getVersionNo());
                break;
            }

            // deploy
            AbstractScript script = DeployHandler.getDeploy(appInfo.getDeployTypeEnum());
            script.setOs(OperatingSystemFactory.build());
            script.setParams(
                new String[]{
                    versionInfo.getFilePath(),
                    appInfo.getWorkPath(),
                    versionInfo.getFileName()
                }
            );
            script.deploy();

            super.setProgressNeelyComplete(); // progress 95%

            // 发布完成更新serviceInfo 信息
            appInfo.setVersionNo(versionInfo.getVersionNo());
            serviceInfo.setLocalServices(JSON.toJSONString(localServices));
            serviceInfo.toFile();

            super.setProgressComplete(); // progress 100%

            break;
        }
    }
}
