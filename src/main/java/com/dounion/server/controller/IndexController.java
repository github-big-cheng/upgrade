package com.dounion.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.dounion.server.Main;
import com.dounion.server.core.base.AppInfo;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.request.MappingConfigHandler;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.deploy.app.AbstractScript;
import com.dounion.server.deploy.app.impl.RestartScript;
import com.dounion.server.deploy.os.OperatingSystemFactory;
import com.dounion.server.eum.ResponseTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);


    @Autowired
    private ServiceInfo serviceInfo;

    /**
     * 功能列表页
     *
     * @return
     */
    @RequestMapping(name = "功能列表", value = "/list")
    public String list() {
        return "list.html";
    }


    /**
     * 查询当前所有功能信息
     *
     * @return
     */
    @RequestMapping(value = "/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson() {
        Map<URI, Object> result = new HashMap<>();
        result.putAll(MappingConfigHandler.mapping);
        // 下载地址
        try {
            final URI uri = new URI(Constant.URL_DOWNLOAD);
            result.put(uri, new HashMap() {{
                put("desc", "下载文件列表");
                put("path", uri.getPath());
            }});
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ResponseBuilder.buildSuccess(result);
    }

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping(name = "首页", value = "/index")
    public String index() {
        return "index.html";
    }


    /**
     * 获取当前主机信息
     *
     * @return
     */
    @RequestMapping("/index.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object indexJson() {
        return ResponseBuilder.buildSuccess(serviceInfo);
    }

    /**
     * serviceInfo.json文件下载
     */
    @RequestMapping("/index/download.file")
    @ResponseType(ResponseTypeEnum.FILE)
    public File download() {
        return new File(Constant.PATH_CONF + Constant.FILE_JSON_CONFIG_NAME);
    }

    /**
     * serviceInfo.json文件下载
     */
    @RequestMapping("/index/update.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object updateJSON(File file) {

        if (file == null) {
            return ResponseBuilder.buildError("文件上传失败");
        }

        ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);

        String message = ResponseBuilder.SUCCESS_MESSAGE;
        try {
            // 获取上传文件信息
            ServiceInfo newInfo = JSONObject.parseObject(Files.readAllBytes(file.toPath()), ServiceInfo.class);

            if (!StringUtils.equals(serviceInfo.getLocalIp(), newInfo.getLocalIp()) ||
                    !serviceInfo.getPort().equals(newInfo.getPort())) {
                message = "警告：文件已更新，修改IP或PORT需要重启服务";
            }

            // 生成文件
            newInfo.toFile();
            // 刷新服务信息
            BeanUtils.copyProperties(newInfo, serviceInfo);
            serviceInfo.setMaster(newInfo.getMaster());
            serviceInfo.setStandBy(newInfo.getStandBy());

            // 向主机刷新服务订阅信息
            TaskHandler.callTask(Constant.TASK_SUBSCRIBE);
            if (serviceInfo.getStandByBlur()) {
                TaskHandler.wakeUp(Constant.TASK_ROUTE);
            }

        } catch (IOException e) {
            return ResponseBuilder.buildError("文件解析失败");
        }
        return ResponseBuilder.buildSuccess(message);
    }


    @ResponseType(ResponseTypeEnum.JSON)
    @RequestMapping(value = "/restart.json")
    public Object restart() {

        final String url = "http://" + serviceInfo.getLocalIp() + ":" + serviceInfo.getPort() + Constant.URL_INDEX;

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000l);
                    Main.restart();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return ResponseBuilder.buildSuccess("重启后台任务已提交，预计5秒后重启", url);
    }


    @RequestMapping("/subscribe.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object subscribe() {
        TaskHandler.callTask(Constant.TASK_SUBSCRIBE);
        return ResponseBuilder.buildSuccess();
    }



    @RequestMapping("/restartApp.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object restartApp(String appType){

        AppInfo app = serviceInfo.appInfoPickUp(appType);
        if(app == null){
            return ResponseBuilder.buildError("未找到指定的本地服务【" + appType + "】");
        }

        AbstractScript.ScriptParams params = new AbstractScript.ScriptParams();
        params.setAppInfo(app);

        AbstractScript script = new RestartScript();
        script.setOs(OperatingSystemFactory.build());
        script.setParams(params);
        script.deploy();

        return ResponseBuilder.buildSuccess();
    }

}
