package com.dounion.server.controller;

import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.entity.DownloadRouteRecord;
import com.dounion.server.service.DownloadRouteRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 下载路由控制器
 */
@Controller
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private DownloadRouteRecordService downloadRouteRecordService;


    @RequestMapping("/register.json")
    public Object registerJson(DownloadRouteRecord record){
        downloadRouteRecordService.updateDownloadRoute(record);
        return ResponseBuilder.buildSuccess();
    }


}
