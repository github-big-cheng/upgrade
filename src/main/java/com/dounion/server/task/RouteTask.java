package com.dounion.server.task;

import com.alibaba.fastjson.JSON;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.service.VersionInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载路由后台任务
 */
@Task(Constant.TASK_ROUTE)
public class RouteTask extends BaseTask {

    @Autowired
    private ServiceInfo serviceInfo;
    @Autowired
    private VersionInfoService versionInfoService;


    @Override
    public String getTaskName() {
        return "下载路由后台任务";
    }

    @Override
    public boolean isLoop() {
        return true;
    }

    @Override
    public long getLoopDelay() {
        return ConfigurationHelper.getLong(Constant.CONF_ROUTE_REGISTER_DELAY, 24 * 60 * 60 * 1000l);
    }

    @Override
    protected void execute() throws Exception {

        if(!serviceInfo.getStandByBlur()){
            logger.info("【{}】 this sever is not stand by server, task will exists", this);
            return;
        }

        if(StringUtils.isBlank(serviceInfo.getMasterIp()) || serviceInfo.getMasterPort()==null){
            logger.info("【{}】 master's ip and port not config, task will exists", this);
            return;
        }

        // progress 10%
        this.setProgressJustStart();

        VersionInfo query = new VersionInfo();
        query.setStatus("1"); // 状态正常
        query.setAddSource("2"); // 远程发布
        List<VersionInfo> versionInfos = versionInfoService.list(query);
        if(CollectionUtils.isEmpty(versionInfos)){
            logger.info("【{}】 no version found to route, task will exists", this);
            return;
        }

        // progress 25%
        this.setProgressTwentyFive();

        final String hostAddress = "http://" + serviceInfo.getLocalIp() + ":" +serviceInfo.getPort();
        for(VersionInfo versionInfo : versionInfos){
            try {
                // 检查文件是否已下载完成
                if(StringUtils.isBlank(versionInfo.getFilePath())){
                    logger.info("【{}】【version record:{}】file not download yet, loop next one", this, versionInfo.getId());
                    continue;
                }

                Map<String, Object> params = new HashMap<>();
                params.put("host", serviceInfo.getLocalIp());
                params.put("port", serviceInfo.getPort());
                params.put("versionNo", versionInfo.getVersionNo());
                params.put("appType", versionInfo.getAppType());
                params.put("fileName", versionInfo.getFileName());
                params.put("path", Constant.URL_DOWNLOAD + versionInfo.getFileName());
                params.put("downloadPath", hostAddress + Constant.URL_DOWNLOAD + versionInfo.getFileName());

                // 调用接口
                NettyClient masterClient = NettyClient.getMasterInstance();
                String result =
                        masterClient.doHttpRequest(
                                NettyClient.buildPostMap(Constant.URL_ROUTE, JSON.toJSONString(params)));

                logger.info("【{}】【params={}】【result={}】", this, params, result);

            } catch (Exception e) {
                logger.error("【{}】 item in loop error:{}", this, e);
            }

        }
    }
}
