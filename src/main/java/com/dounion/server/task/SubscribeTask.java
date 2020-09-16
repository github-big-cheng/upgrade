package com.dounion.server.task;

import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.base.AppInfo;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.service.SubscribeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.dounion.server.core.base.Constant.URL_SUBSCRIBE;

/**
 * 向主机订阅更新服务
 */
@Task(Constant.TASK_SUBSCRIBE)
public class SubscribeTask extends BaseTask {

    @Override
    public String getTaskName() {
        return "服务订阅后台任务";
    }

    @Autowired
    private ServiceInfo serviceInfo;
    @Autowired
    private SubscribeService subscribeService;

    @Override
    public void execute() throws Exception {

        // 获取当前服务信息
        if (serviceInfo.getMasterBlur()) {
            // 配置上级主机IP/PORT 调用取消订阅接口
            if (StringUtils.isNotBlank(serviceInfo.getMasterIp()) &&
                    serviceInfo.getMasterPort() != null) {
                logger.info("current service is master, and master ip/port is config, unSubscribe task been called");
                // 取消订阅
                TaskHandler.callTask(Constant.TASK_UN_SUBSCRIBE);
                return;
            }

            logger.info("current service is master, service subscribe end");
            return;
        }

        this.setProgressTwentyFive();

        Map<String, String> servicesMap = new HashMap<>();

        // 查询向当前主机被订阅的服务列表(去重)
        SubscribeInfo query = new SubscribeInfo();
        query.setStatus("1");
        List<Map<String, String>> subscribeList = subscribeService.currentServiceSubscribeQuery(query);
        if (!CollectionUtils.isEmpty(subscribeList)) {
            for(Map<String, String> item : subscribeList){
                servicesMap.put(item.get("APP_TYPE"), item.get("VERSION_NO"));
            }
        }

        // 本地服务列表
        List<AppInfo> appInfoList = serviceInfo.getLocalServices();
        if (!CollectionUtils.isEmpty(appInfoList)) {
            for (AppInfo appInfo : appInfoList) {
                servicesMap.put(appInfo.getAppType(), appInfo.getVersionNo());
            }
        }

        // 封装应用类型及版本号
        StringBuffer appTypeSb = new StringBuffer();
        StringBuffer versionNosSb = new StringBuffer();
        for(String appType : servicesMap.keySet()){
            appTypeSb.append(appType).append(",");
            versionNosSb.append(servicesMap.get(appType)).append(",");
        }
        appTypeSb.setLength(appTypeSb.length()-1);
        versionNosSb.setLength(versionNosSb.length()-1);


        if (super.isInterrupted()) {
            logger.info("subscribe task 【{}】 has been interrupted, it will be exit...", this.taskId);
            return;
        }

        // progress 75%
        this.setProgressSeventyFive();

        // 调用主机订阅接口
        Map<String, Object> params = new HashMap<>();
        params.put("code", serviceInfo.getCode());
        params.put("name", serviceInfo.getName());
        params.put("osType", serviceInfo.getOsType());
        params.put("appType", appTypeSb.toString()); // 应用类型
        params.put("versionNo", versionNosSb.toString()); // 版本号
        params.put("isStandBy", serviceInfo.getStandBy());
        params.put("publishUrl", serviceInfo.getPublishPath());

        String json = JSONObject.toJSONString(params);
        NettyClient client = NettyClient.getMasterInstance();
        String result = client.doHttpRequest(NettyClient.buildPostMap(URL_SUBSCRIBE, json));

        logger.info("【{}】, result is 【{}】", this, result);

        this.setProgressComplete(); // progress 100%
    }
}
