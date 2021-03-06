package com.dounion.server.controller;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.FileHelper;
import com.dounion.server.core.helper.StringHelper;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.route.RouteHandler;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.eum.AppTypeEnum;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.service.VersionInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/version")
public class VersionController {

    private final static Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private ServiceInfo serviceInfo;
    @Autowired
    private VersionInfoService versionInfoService;

    /**
     * 版本列表页面
     * @return
     */
    @RequestMapping(name = "版本列表", value = "/list")
    public String list(){
        return "version/list.html";
    }


    /**
     * 版本列表查询
     * @param query
     * @return
     */
    @RequestMapping("/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(VersionInfo query){
        return ResponseBuilder.buildSuccess(versionInfoService.list(query));
    }

    /**
     * 版本新增页面
     * @return
     */
    @RequestMapping(name = "版本新增", value = "/add")
    public String add(){
        return "version/add.html";
    }


    /**
     * 版本新增
     * @return
     */
    @RequestMapping("/add.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object addJson(VersionInfo record, File file){

        // 是否远程发布
        boolean isRemotePublish = StringUtils.equals(record.getAddSource(), "2");
        logger.debug("is current mode remote publish {}", isRemotePublish);
        if(!isRemotePublish){
            // 本地上传 检查文件
            if(file == null){
                return ResponseBuilder.buildError("请上传文件");
            }

            AppTypeEnum appTypeEnum = AppTypeEnum.getMap().get(record.getAppType());
            if(!StringUtils.equals(appTypeEnum.getFileName(), file.getName())){
                return ResponseBuilder.buildError("文件名称与应用类型检查不通过");
            }

            // 初始化文件信息
            record.setFileName(file.getName()); // 文件名称
            record.setFilePath(file.getPath()); // 文件路径
            record.setFileSize(file.length()); // 文件大小
            record.setFileMd5(FileHelper.getFileMD5(file)); // 文件MD5值
        } else {
            // 远程发布检查 检查数据库中保存的版本号
            VersionInfo query = new VersionInfo();
            query.setAppType(record.getAppType());
            query.setStatus("1");
            List<VersionInfo> list = versionInfoService.list(query);
            if(!CollectionUtils.isEmpty(list) &&
                    StringUtils.isNotBlank(list.get(0).getFilePath()) && // 文件路径不为空
                    list.get(0).getVersionNo().compareTo(record.getVersionNo()) >= 0){ // 版本号比较
                logger.info("接收发布成功，但版本过低已忽略，local version is {}, remote version is{}",
                        list.get(0).getVersionNo(), record.getVersionNo());
                return ResponseBuilder.buildSuccess("接收发布成功，但版本过低已忽略");
            }
        }

        // 更新版本信息
        final int versionId = versionInfoService.updateVersion(record);
        logger.trace("new version id is 【{}】", versionId);


        // 路由注销
        RouteHandler.routeCancel(Constant.URL_DOWNLOAD + record.getFileName());

        // 后台任务-任务链
        ConcurrentHashMap<String, Object> taskParams = new ConcurrentHashMap<>();
        taskParams.put("versionId", versionId);
        taskParams.put("lockKey", record.getAppType());

        List<String> tasks = new ArrayList<>();
        if(isRemotePublish){
            // 远程下载
            tasks.add(Constant.TASK_DOWNLOAD);
        }

        // 非强制更新
        if(StringUtils.equals(record.getIsForceUpdate(), "1") || // 强制更新
                !StringUtils.equals(serviceInfo.getIgnoreMode(), "1")){ // 非忽略模式
            if(StringUtils.equals(record.getPublishType(), "2")){ // 非手动发布
                // 本地部署
                tasks.add(Constant.TASK_DEPLOY);
            }
        }

        // 自动发布 - 调度任务:发布通知
        if(StringUtils.equals(record.getPublishType(), "2")){
            tasks.add(Constant.TASK_PUBLISH_AUTO);
        }

        // 检查是否需要后台任务
        logger.trace("tasks is :{}", tasks);
        if(!CollectionUtils.isEmpty(tasks)){
            if(tasks.size() == 1){
                // 单任务
                TaskHandler.callTask(tasks.get(0), taskParams);
            } else {
                // 调用任务链
                TaskHandler.callTaskChain(taskParams, tasks.toArray(new String[tasks.size()]));
            }
        }

        return ResponseBuilder.buildSuccess();
    }


    /**
     * 版本状态变更
     * @return
     */
    @RequestMapping("/updateStatus.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object updateStatus(int id, String status){
        // 更新版本信息
        VersionInfo record = new VersionInfo();
        record.setId(id);
        record.setStatus(status);
        versionInfoService.update(record);
        return ResponseBuilder.buildSuccess();
    }



    @RequestMapping("/redeploy.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object redeploy(int id){
        VersionInfo entity = versionInfoService.selectById(id);
        if(entity == null){
            return ResponseBuilder.buildError("版本记录未找到【"+id+"】");
        }

        // 自动发布 - 调度任务:发布通知
        if(StringUtils.equals(entity.getPublishType(), "2")){
            TaskHandler.callTask(Constant.TASK_PUBLISH_AUTO);
        }

        ConcurrentHashMap<String, Object> taskParams = new ConcurrentHashMap<>();
        taskParams.put("versionId", id);

        if(StringUtils.equals(entity.getAddSource(), "2")){
            // 文件下载
            TaskHandler.callTaskChain(taskParams, Constant.TASK_DOWNLOAD, Constant.TASK_DEPLOY);
        } else {
            TaskHandler.callTask(Constant.TASK_DEPLOY, taskParams);
        }

        return ResponseBuilder.buildSuccess();
    }
}
