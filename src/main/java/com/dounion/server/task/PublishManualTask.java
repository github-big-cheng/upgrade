package com.dounion.server.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.helper.DataHelper;
import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.entity.UpgradeRecord;
import com.dounion.server.service.UpgradeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
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
            throw new SystemException("params can not be empty");
        }

        UpgradeRecord query = new UpgradeRecord();
        query.setVersionId((Integer) super.params.get("versionId")); // 根据版本号查询
        query.setNotifyCountStr(ConfigurationHelper.getString(Constant.CONF_PUBLISH_MAX_NOTIFY_COUNT, "1"));
        List<UpgradeRecord> records =
                upgradeRecordService.selectEntityListBySelective(query);
        if(CollectionUtils.isEmpty(records)){
            logger.warn("【{}】, upgrade list is empty", this);
            throw new SystemException("upgrade list is empty");
        }

        this.setProgressTwentyFive(); // progress 25%
        BigDecimal itemProcess = DataHelper.divide(90, records.size());

        String time = DateHelper.format(new Date());
        for(UpgradeRecord record : records){
            try{
                if(record.getVersion() == null ||
                            record.getSubscribe() == null){
                    logger.warn("【{}】, 记录【{}】缺少必要实体", this, record.getId());
                    continue;
                }

                record.setNotifyStatus("0");
                record.setNotifyCountStr("1");
                record.setNotifyTime(time);

                Map<String, Object> params = new HashMap<>();
                params.put("versionNo", record.getVersionNo()); // 版本号
                params.put("appType", record.getAppType()); // 应用类型
                params.put("fileName", record.getFileName()); // 文件名称
                params.put("fileSize", record.getFileSize()); // 文件大小
                params.put("fileMd5", record.getFileMd5()); // 文件MD5
                params.put("isForceUpdate", record.getIsForceUpdate()); // 是否强制更新
                params.put("publishType", "2"); // 发布类型 默认自动发布
                params.put("addSource", "2"); // 远程发布

                // 通知发布
                String publishUrl = record.getSubscribe().getPublishUrl();
                String message = JSONObject.toJSONString(params);
                String result = NettyClient.getInstance(publishUrl)
                        .doHttpRequest(NettyClient.buildPostMap(Constant.URL_PUBLISH, message));

                logger.info("manual publish task result:【{}】", result);

                Map<String, Object> rst = JSON.parseObject(result, Map.class);
                if(rst != null && (Integer) rst.get("code") == 0){
                    record.setNotifyStatus("1");
                }

            } catch (Exception e) {
                logger.error("【{}】 error in loop :{}", this, e);
            } finally {
                upgradeRecordService.updateBySelective(record);

                // progress
                this.setProgress(DataHelper.add(this.getProgress(), itemProcess));
            }

        }
    }
}
