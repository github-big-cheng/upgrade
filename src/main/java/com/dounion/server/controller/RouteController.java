package com.dounion.server.controller;

import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.route.RouteHandler;
import com.dounion.server.entity.DownloadRouteRecord;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.service.DownloadRouteRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 下载路由控制器
 */
@Controller
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private DownloadRouteRecordService downloadRouteRecordService;


    @RequestMapping("/register.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object registerJson(DownloadRouteRecord record){
        downloadRouteRecordService.updateDownloadRoute(record);
        return ResponseBuilder.buildSuccess();
    }


    @RequestMapping("/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(){
        Map<String, Object> result = new HashMap<>();
        result.put("local", RouteHandler.LOCAL_COUNTER_MAP);
        result.put("routeInfo", RouteHandler.ROUTE_INFO_MAP);
        result.put("routeQueue", RouteHandler.ROUTE_QUEUE_MAP);
        result.put("downloadProgress", RouteHandler.DOWNLOAD_PROGRESS_MAP);
        return ResponseBuilder.buildSuccess(result);
    }


}
