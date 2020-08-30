package com.dounion.server.task;

import com.dounion.server.core.base.AppInfo;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 想主机订阅更新服务
 *
 */
public class SubscribeTask extends Thread{

    private final static Logger logger = LoggerFactory.getLogger(SubscribeTask.class);

    @Override
    public void run() {

        try {
            // 获取当前服务信息
            ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);
            if(serviceInfo.getMasterBlur()){
                logger.info("current service is master, service subscribe end");
                return;
            }

            Assert.notNull(serviceInfo.getMasterService(), "master service address must config");

            Set<String> servicesSet = new HashSet<>();

            // 本地服务列表
            List<AppInfo> appInfoList = serviceInfo.getLocalServiceList();
            if(!CollectionUtils.isEmpty(appInfoList)){
                for(AppInfo appInfo : appInfoList){
                    servicesSet.add(appInfo.getServiceType());
                }
            }


            // 查询向当前主机被订阅的服务列表(去重)
            SubscribeInfoMapper subscribeInfoMapper = SpringApp.getInstance().getBean(SubscribeInfoMapper.class);
            SubscribeInfo query = new SubscribeInfo();
            query.setStatus("1");
            List<String> subscribeList = subscribeInfoMapper.currentServiceSubscribeQuery(query);
            if(!CollectionUtils.isEmpty(subscribeList)){
                servicesSet.addAll(subscribeList);
            }

            // 调用主机订阅接口
            Map<String, Object> params = new HashMap<>();
            params.put("code", serviceInfo.getCode());
            params.put("osType", serviceInfo.getOsType());
            params.put("serviceType", StringUtils.join(servicesSet, ","));
            params.put("standBy", serviceInfo.getStandBy());
            params.put("publishUrl", serviceInfo.getPublishPath());


        } catch (Exception e) {
            logger.error("service subscribe task error:{}", e);
        }

    }
}
