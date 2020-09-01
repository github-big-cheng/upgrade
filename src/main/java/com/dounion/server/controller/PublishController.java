package com.dounion.server.controller;

import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.eum.ResponseTypeEnum;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/publish")
public class PublishController {

    /**
     * 推送列表页
     * @return
     */
    @RequestMapping("/list")
    public String list(){
        return "publish/list.html";
    }


    /**
     * 订阅回调地址
     * @return
     */
    @RequestMapping("/callback")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object callbackJson(){



        return ResponseBuilder.buildSuccess();
    }

}
