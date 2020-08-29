package com.dounion.server.task;

import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务启动后向master注册
 *      仅一次，执行完即结束线程
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
            }

            // 查询向当前主机订阅更新的服务列表(去重)
            SubscribeInfoMapper subscribeInfoMapper = SpringApp.getInstance().getBean(SubscribeInfoMapper.class);
            SubscribeInfo query = new SubscribeInfo();
            query.setStatus("");

        } catch (Exception e) {
            logger.error("service subscribe task error:{}", e);
        } finally {

        }


    }
}
