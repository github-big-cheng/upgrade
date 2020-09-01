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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.dounion.server.core.base.Constant.URL_SUBSCRIBE;

/**
 * 向主机订阅更新服务
 */
@Task(Constant.TASK_SUBSCRIBE)
public class SubscribeTask extends BaseTask {

    private final static Logger logger = LoggerFactory.getLogger(SubscribeTask.class);

    @Override
    public String getTaskName() {
        return "服务订阅后台任务";
    }

    @Autowired
    private ServiceInfo serviceInfo;
    @Autowired
    private SubscribeService subscribeService;

    @Override
    public void execute() {
        try {

            // 获取当前服务信息
            if (serviceInfo.getMasterBlur()) {
                // 配置上级主机IP/PORT 调用取消订阅接口
                if(StringUtils.isNotBlank(serviceInfo.getMasterIp()) &&
                            serviceInfo.getMasterPort() != null){
                    logger.info("current service is master, and master ip/port is config, unSubscribe task been called");
                    TaskHandler.callTask(Constant.TASK_UN_SUBSCRIBE);
                    return;
                }

                logger.info("current service is master, service subscribe end");
                return;
            }

            Set<String> servicesSet = new HashSet<>();

            // 本地服务列表
            List<AppInfo> appInfoList = serviceInfo.getLocalServiceList();
            if (!CollectionUtils.isEmpty(appInfoList)) {
                for (AppInfo appInfo : appInfoList) {
                    servicesSet.add(appInfo.getServiceType());
                }
            }


            // 查询向当前主机被订阅的服务列表(去重)
            SubscribeInfo query = new SubscribeInfo();
            query.setStatus("1");
            List<String> subscribeList = subscribeService.currentServiceSubscribeQuery(query);
            if (!CollectionUtils.isEmpty(subscribeList)) {
                servicesSet.addAll(subscribeList);
            }


            if (super.isInterrupted()) {
                logger.info("subscribe task [{}] has been interrupted, it will be exit...", this.taskId);
                return;
            }

            // 调用主机订阅接口
            Map<String, Object> params = new HashMap<>();
            params.put("code", serviceInfo.getCode());
            params.put("osType", serviceInfo.getOsType());
            params.put("appType", StringUtils.join(servicesSet, ","));
            params.put("isStandBy", serviceInfo.getStandBy());
            params.put("publishUrl", serviceInfo.getPublishPath());

            String json = JSONObject.toJSONString(params);
            NettyClient client = NettyClient.getMasterInstance();
            String result = client.doHttpRequest(NettyClient.buildPostMap(URL_SUBSCRIBE, json));

            logger.info("SubscribeTask:[{}], result is [{}]", this, result);

        } catch (Exception e) {
            logger.error("service subscribe task error:{}", e);
        }
        return;
    }
}
