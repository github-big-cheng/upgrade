package com.dounion.server.controller;

import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.request.HandlerMappingConfig;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.eum.ResponseTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class IndexController {

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * 功能列表页
     * @return
     */
    @RequestMapping(name = "功能列表", value = "/list")
    public String list(){
        return "list.html";
    }


    /**
     * 查询当前所有功能信息
     * @return
     */
    @RequestMapping(value = "/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(){
        return ResponseBuilder.buildSuccess(HandlerMappingConfig.mapping);
    }

    /**
     * 首页
     * @return
     */
    @RequestMapping(name="首页", value = "/index")
    public String index(){
        return "page/index.html";
    }


    /**
     * 获取当前主机信息
     * @return
     */
    @RequestMapping(value = "/index.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object indexJson(){
        return ResponseBuilder.buildSuccess(SpringApp.getInstance().getBean(ServiceInfo.class));
    }

}
