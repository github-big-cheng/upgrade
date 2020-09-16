package com.dounion.server.controller;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.service.SubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

@Controller
@RequestMapping("/subscribe")
public class SubscribeController {

    private Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @Autowired
    private SubscribeService subscribeService;


    /**
     * 订阅列页面
     * @return
     */
    @RequestMapping(name = "订阅列表", value = "/list")
    public String list(){
        return "subscribe/list.html";
    }


    @RequestMapping("/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(SubscribeInfo query, int pageNo, int pageSize){
        return ResponseBuilder.buildSuccess(subscribeService.page(query, pageNo, pageSize));
    }


    /**
     * 订阅页面
     * @return
     */
    @RequestMapping(name = "订阅", value = "/add")
    public String add(){
        return "subscribe/add.html";
    }


    /**
     * 添加订阅记录
     * @return
     */
    @RequestMapping(value = "/add.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object addJson(SubscribeInfo record){
        // 更新订阅记录
        subscribeService.addSubscribe(record);
        // 自动发布任务
        TaskHandler.callTask(Constant.TASK_PUBLISH_AUTO);
        return ResponseBuilder.buildSuccess();
    }


    /**
     * 取消远程订阅
     * @return
     */
    @RequestMapping("/unSubscribe.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object unSubscribe(){
        TaskHandler.callTask(Constant.TASK_UN_SUBSCRIBE);
        return ResponseBuilder.buildSuccess("已提交取消订阅任务");
    }


    /**
     * 本地订阅列表撤销
     * @param code
     * @return
     */
    @RequestMapping("/cancel.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object cancelJson(String code){
        Assert.notNull(code, "code must not be null");
        SubscribeInfo record = new SubscribeInfo();
        record.setCode(code);
        subscribeService.deleteBySelective(record);
        return ResponseBuilder.buildSuccess();
    }

}
