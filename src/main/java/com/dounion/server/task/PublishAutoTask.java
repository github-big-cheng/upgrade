package com.dounion.server.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.helper.DataHelper;
import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.entity.UpgradeRecord;
import com.dounion.server.service.UpgradeRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动发布后台任务
 */
@Task(Constant.TASK_PUBLISH_AUTO)
public class PublishAutoTask extends BaseTask {

    @Autowired
    private UpgradeRecordService upgradeRecordService;

    @Override
    public String getTaskName() {
        return "自动发布后台任务";
    }

    @Override
    protected void execute() throws Exception {

        // 推送前，生成个更新记录信息
        Map<String, Object> query = new HashMap<>();
        query.put("maxCount", ConfigurationHelper.getInt(Constant.CONF_PUBLISH_MAX_NOTIFY_COUNT, 1));
        List<Map<String, Object>> list = upgradeRecordService.publishListQuery(query);
        if(CollectionUtils.isEmpty(list)){
            logger.info("no publish record found");
            return;
        }

        // progress
        this.setProgressJustStart();
        BigDecimal itemProcess = DataHelper.divide(90, list.size());

        UpgradeRecord record = null;
        for(Map<String, Object> item : list){
            try {
                record = new UpgradeRecord();

                // 最后更新时间
                record.setNotifyTime(DateHelper.format(new Date()));
                record.setNotifyStatus("0");
                record.setNotifyCountStr("1");

                Integer recordId = (Integer) item.get("RECORD_ID");
                if(recordId == null){
                    // 新增
                    record.setSubscribeId((Integer) item.get("SUBSCRIBE_ID"));
                    record.setCode((String) item.get("CODE"));
                    record.setName((String) item.get("NAME"));
                    record.setPublishUrl((String) item.get("PUBLISH_URL"));
                    record.setVersionId((Integer) item.get("VERSION_ID"));
                    record.setAppType((String) item.get("APP_TYPE"));
                    record.setVersionNo((String) item.get("VERSION_NO"));
                    record.setPublishType((String) item.get("PUBLISH_TYPE"));
                    record.setNotifyCount(0);
                    upgradeRecordService.insert(record);
                } else {
                    // 更新
                    record.setId(recordId);
                }

                Map<String, Object> params = new HashMap<>();
                params.put("versionNo", item.get("VERSION_NO")); // 版本号
                params.put("appType", item.get("APP_TYPE")); // 应用类型
                params.put("fileSize", item.get("FILE_SIZE")); // 文件大小
                params.put("fileMd5", item.get("FILE_MD5")); // 文件MD5
                params.put("isForceUpdate", item.get("IS_FORCE_UPDATE")); // 是否强制更新
                params.put("addSource", "2"); // 远程发布

                // 通知发布
                String publishUrl = (String) item.get("PUBLISH_URL");
                String message = JSONObject.toJSONString(params);
                String result = NettyClient.getInstance(publishUrl)
                        .doHttpRequest(NettyClient.buildPostMap(Constant.URL_PUBLISH, message));

                logger.info("publish task result:【{}】", result);

                Map<String, Object> rst = JSON.parseObject(result, Map.class);
                record.setNotifyStatus("0");
                if(rst != null && StringUtils.equals((String) rst.get("code"), "0")){
                    record.setNotifyStatus("1");
                }

            } catch (Exception e) {
                logger.error("record publish failed..{}", item);
            } finally {
                if(record != null && record.getId()!=null){
                    upgradeRecordService.updateBySelective(record);
                }
                // 设置执行进度
                this.setProgress(DataHelper.add(this.getProgress(), itemProcess));
            }

        }

    }
}
