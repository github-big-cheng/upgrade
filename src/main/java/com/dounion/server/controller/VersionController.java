package com.dounion.server.controller;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.helper.FileHelper;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.service.VersionInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.HashMap;

@Controller
@RequestMapping("/version")
public class VersionController {

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
    public Object addJson(final VersionInfo record, File file){

        // 远程发布没有file
        if(file != null){
            record.setFilePath(file.getPath()); // 文件路径
            record.setFileSize(file.length()); // 文件大小
            record.setFileMd5(FileHelper.getFileMD5(file)); // 文件MD5值
        }
        // 更新版本信息
        versionInfoService.updateVersion(record);

        // 自动发布 - 调度任务:发布通知
        if(StringUtils.equals(record.getPublishType(), "2")){
            TaskHandler.callTask(Constant.TASK_PUBLISH_AUTO);
        }

        // 是否远程发布
        boolean isRemotePublish = StringUtils.equals(record.getAddSource(), "2");
        if(isRemotePublish){
            TaskHandler.callTaskChain(
                new HashMap(){{ put("versionId", record.getId()); }}, // 版本ID
                Constant.TASK_DOWNLOAD, Constant.TASK_DEPLOY
            );
        } else {
            // 文件下载
            TaskHandler.callTask(Constant.TASK_DEPLOY, new HashMap(){{
                put("versionId", record.getId()); // 版本ID
            }});
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
}
