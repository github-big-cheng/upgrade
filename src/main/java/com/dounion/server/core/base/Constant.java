package com.dounion.server.core.base;

import com.dounion.server.core.helper.ConfigurationHelper;

import java.io.File;

public interface Constant {

    // ==========================================  文件路径相关 ==========================================================

    // 服务配置文件名称
    String JSON_CONFIG_FILE_NAME = "serviceInfo.json";

    // 临时文件夹
    String TEMP_PATH = System.getProperty("java.io.tmpdir");
    // 工作目录
//    String WORK_PATH = System.getProperty("user.dir") + File.separator;
    String PATH_WORK = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\";
    // 配置文件路径
    String PATH_CONF = PATH_WORK + "conf" + File.separator;
    // 静态文件路径
    String PATH_WEB = PATH_WORK + "web" + File.separator;
    // html文件路径
    String PATH_PAGE = PATH_WEB + "page" + File.separator;
    // 下载路径
    String PATH_DOWNLOAD = ConfigurationHelper.getString("download_path") + File.separator;


    // ==========================================  请求地址相关 ==========================================================

    // 下载请求路径
    String URL_DOWNLOAD = "/download";
    // 订阅服务地址
    String URL_SUBSCRIBE = "/subscribe/add.json";

    // ==========================================  后台任务相关 ==========================================================

    // 订阅任务
    String SUBSCRIBE_TASk = "subscribeTask";

}
