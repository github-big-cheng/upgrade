package com.dounion.server.controller;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.entity.UpgradeRecord;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.service.UpgradeRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
@RequestMapping("/publish")
public class PublishController {

    @Autowired
    private UpgradeRecordService upgradeRecordService;


    /**
     * 发布列表页
     * @return
     */
    @RequestMapping(name = "发布记录", value = "/list")
    public String list(){
        return "publish/list.html";
    }


    @RequestMapping("/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(UpgradeRecord query){
        return ResponseBuilder.buildSuccess(upgradeRecordService.selectEntityListBySelective(query));
    }


    @RequestMapping(name = "手工发布", value = "/add")
    public String add(){
        return "publish/add.html";
    }

    @RequestMapping("/add.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object addJson(final VersionInfo version, String subscribeCodes){

        if(StringUtils.isBlank(subscribeCodes)){
            return ResponseBuilder.buildError("请选择要发布的库点");
        }

        // 生成发布记录
        upgradeRecordService.upgradeRecordsGenerate(version, subscribeCodes);

        // 调度任务: 手工发布
        TaskHandler.callTask(Constant.TASK_PUBLISH_MANUAL, new HashMap(){{
            put("versionId", version.getId());
        }});

        return ResponseBuilder.buildSuccess();
    }

}
