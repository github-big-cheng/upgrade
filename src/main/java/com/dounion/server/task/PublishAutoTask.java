package com.dounion.server.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.helper.DataHelper;
import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.route.RouteHandler;
import com.dounion.server.core.task.LockHandler;
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

        try {
            LockHandler.lock(this.getTaskName());

            // 推送前，生成个更新记录信息
            Map<String, Object> query = new HashMap<>();
            if(!CollectionUtils.isEmpty(super.params)){
                query.putAll(super.params);
            }
            query.put("publishType", "2"); // 默认自动发布类型
            query.put("maxCount", ConfigurationHelper.getInt(Constant.CONF_PUBLISH_MAX_NOTIFY_COUNT, 1));

            logger.trace("PublishAutoTask.execute query is :{}", query);

            List<Map<String, Object>> list = upgradeRecordService.publishListQuery(query);
            if(CollectionUtils.isEmpty(list)){
                logger.info("【{}】 no publish record found, task will exit", this);
                return;
            }

            // progress
            this.setProgressJustStart();
            BigDecimal itemProcess = DataHelper.divide(90, list.size());

            UpgradeRecord record = null;
            int index = 0;
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
                        record.setFileName((String) item.get("FILE_NAME"));
                        record.setFileSize(Long.valueOf((Integer) item.get("FILE_SIZE")));
                        record.setFileMd5((String) item.get("FILE_MD5"));
                        record.setPublishType((String) item.get("PUBLISH_TYPE"));
                        record.setNotifyCount(0);
                        upgradeRecordService.insert(record);
                    } else {
                        // 更新
                        record.setId(recordId);
                    }

                    long fileSize = DataHelper.getBigDecimal(item.get("FILE_SIZE")).longValue();
                    String fileName = (String) item.get("FILE_NAME");

                    Map<String, Object> params = new HashMap<>();
                    params.put("versionNo", item.get("VERSION_NO")); // 版本号
                    params.put("appType", item.get("APP_TYPE")); // 应用类型
                    params.put("fileName", item.get("FILE_NAME")); // 文件名称
                    params.put("fileSize", item.get("FILE_SIZE")); // 文件大小
                    params.put("fileMd5", item.get("FILE_MD5")); // 文件MD5
                    params.put("isForceUpdate", item.get("IS_FORCE_UPDATE")); // 是否强制更新
                    params.put("publishType", "2"); // 发布类型 默认自动发布
                    params.put("addSource", "2"); // 远程发布

                    // 发布策略控制
                    RouteHandler.publishPolicy(index, fileSize, Constant.URL_DOWNLOAD + fileName);

                    // 通知发布
                    String publishUrl = (String) item.get("PUBLISH_URL");
                    String message = JSONObject.toJSONString(params);
                    NettyClient client = NettyClient.getInstance(publishUrl);
                    String result = client.doHttpRequest(NettyClient.buildPostMap(Constant.URL_PUBLISH, message));
                    client.close();

                    logger.info("publish task result:【{}】", result);

                    Map<String, Object> rst = JSON.parseObject(result, Map.class);
                    record.setNotifyStatus("0");
                    if(rst != null && (Integer) rst.get("code") == 0){
                        record.setNotifyStatus("1");
                    }

                } catch (Exception e) {
                    logger.error("record 【{}】 publish failed..,{}", item, e);
                } finally {
                    index++;

                    if(record != null && record.getId()!=null){
                        upgradeRecordService.updateBySelective(record);
                    }
                    // 设置执行进度
                    this.setProgress(DataHelper.add(this.getProgress(), itemProcess));
                }

            }
        } finally {
            LockHandler.unlock(this.getTaskName());
        }

    }
}
