package com.dounion.server.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.entity.UpgradeRecord;
import com.dounion.server.service.UpgradeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手动发布后台任务
 */
@Task(Constant.TASK_PUBLISH_MANUAL)
public class PublishManualTask extends BaseTask {

    @Autowired
    private UpgradeRecordService upgradeRecordService;

    @Override
    public String getTaskName() {
        return "手动发布后台任务";
    }

    @Override
    protected void execute() throws Exception{
        if(CollectionUtils.isEmpty(super.params) ||
                super.params.get("versionId")==null){
            logger.error("【{}】, params can not be empty", this);
            return;
        }

        UpgradeRecord query = new UpgradeRecord();
        query.setVersionId((Integer) super.params.get("versionId")); // 根据版本号查询
        List<UpgradeRecord> records =
                upgradeRecordService.selectEntityListBySelective(query);
        if(CollectionUtils.isEmpty(records)){
            logger.warn("【{}】, upgrade list is empty", this);
            return;
        }

        for(UpgradeRecord record : records){
            if(record.getVersion() == null ||
                        record.getSubscribe() == null){
                logger.warn("【{}】, 记录【{}】缺少必要实体", this, record.getId());
                continue;
            }
            try{
                Map<String, Object> params = new HashMap<>();
                params.put("versionNo", record.getVersion().getVersionNo()); // 版本号
                params.put("appType", record.getSubscribe().getAppType()); // 应用类型
                params.put("isForceUpdate", record.getVersion().getIsForceUpdate()); // 是否强制更新
                params.put("addSource", "2"); // 远程发布

                // 通知发布
                String publishUrl = record.getSubscribe().getPublishUrl();
                String message = JSONObject.toJSONString(params);
                String result = NettyClient.getInstance(publishUrl)
                        .doHttpRequest(NettyClient.buildPostMap(Constant.URL_PUBLISH, message));

                logger.info("manual publish task result:【{}】", result);

                Map<String, Object> rst = JSON.parseObject(result, Map.class);
                record.setNotifyStatus("0");
                if(StringUtils.pathEquals((String) rst.get("code"), "0")){
                    record.setNotifyStatus("1");
                }

            } catch (Exception e) {
                logger.error("【{}】 error in loop :{}", this, e);
                record.setNotifyStatus("0");
            } finally {
                record.setNotifyCountStr("1");
                upgradeRecordService.updateBySelective(record);
            }

        }
    }
}
