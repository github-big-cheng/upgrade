package com.dounion.server.task;

import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.annotation.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.dounion.server.core.base.Constant.URL_UN_SUBSCRIBE;

/**
 * 向主机订阅更新服务
 */
@Task(Constant.TASK_UN_SUBSCRIBE)
public class UnSubscribeTask extends BaseTask {

    @Override
    public String getTaskName() {
        return "取消订阅后台任务";
    }

    @Autowired
    private ServiceInfo serviceInfo;

    @Override
    public void execute() throws Exception {
        try {
            // 调用主机订阅接口
            Map<String, Object> params = new HashMap<>();
            params.put("code", serviceInfo.getCode());

            String json = JSONObject.toJSONString(params);
            NettyClient client = NettyClient.getMasterInstance();
            String result = client.doHttpRequest(NettyClient.buildPostMap(URL_UN_SUBSCRIBE, json));

            logger.info("【{}】, result is 【{}】", this, result);

        } catch (Exception e) {
            logger.error("service subscribe task error:{}", e);
        }
        return;
    }
}
