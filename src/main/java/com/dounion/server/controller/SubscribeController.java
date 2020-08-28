package com.dounion.server.controller;

import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.service.SubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
        return "page/subscribe/list.html";
    }


    @RequestMapping("/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(SubscribeInfo query){
        return ResponseBuilder.buildSuccess(subscribeService.list(query));
    }


    /**
     * 订阅页面
     * @return
     */
    @RequestMapping(name = "订阅", value = "/add")
    public String add(){
        return "page/subscribe/add.html";
    }


    /**
     * 添加订阅记录
     * @return
     */
    @RequestMapping(value = "/add.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object addJson(SubscribeInfo record){
        subscribeService.addSubscribe(record);
        return ResponseBuilder.buildSuccess();
    }

}
