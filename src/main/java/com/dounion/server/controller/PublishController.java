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
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/publish")
public class PublishController {


    private final static Logger logger = LoggerFactory.getLogger(PublishController.class);

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
    public Object listJson(UpgradeRecord query, int pageSize, int pageNo){
        PageHelper.startPage(pageNo, pageSize);
        return ResponseBuilder.buildSuccess(upgradeRecordService.page(query, pageSize, pageNo));
    }


    @RequestMapping(name = "手工发布", value = "/add")
    public String add(){
        return "publish/add.html";
    }


    /**
     * 手工发布
     * @param version
     * @param subscribeCodes
     * @return
     */
    @RequestMapping("/add.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object addJson(final VersionInfo version, String subscribeCodes){

        if(StringUtils.isBlank(subscribeCodes)){
            return ResponseBuilder.buildError("请选择要发布的库点");
        }

        // 生成发布记录
        upgradeRecordService.upgradeRecordsGenerate(version, subscribeCodes);

        // 调度任务: 手工发布
        TaskHandler.callTask(Constant.TASK_PUBLISH_MANUAL, new ConcurrentHashMap(){{
            put("versionId", version.getId());
        }});

        return ResponseBuilder.buildSuccess();
    }



    @RequestMapping("/republish.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object republish(){
        TaskHandler.callTask(Constant.TASK_PUBLISH_AUTO);
        return ResponseBuilder.buildSuccess();
    }

}
