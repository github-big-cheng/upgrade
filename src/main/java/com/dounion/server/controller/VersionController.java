package com.dounion.server.controller;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.helper.FileHelper;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.service.VersionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

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
    public Object addJson(VersionInfo record, File file){
        record.setFilePath(file.getPath());
        // 更新版本信息
        versionInfoService.updateVersion(record);
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
