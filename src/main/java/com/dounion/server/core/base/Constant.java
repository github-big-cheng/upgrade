package com.dounion.server.core.base;

import com.dounion.server.core.helper.ConfigurationHelper;

import java.io.File;

public interface Constant {

    // ==========================================  文件路径相关 ==========================================================

    /**
     * 服务配置文件名称
     */
    String FILE_JSON_CONFIG_NAME = "serviceInfo.json";

    /**
     * 临时文件夹
     */
    String PATH_TEMP = System.getProperty("java.io.tmpdir");
    /**
     * 工作目录
     */
//    String PATH_WORK = System.getProperty("user.dir") + File.separator;
    String PATH_WORK = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\";
    /**
     * 配置文件路径
     */
    String PATH_CONF = PATH_WORK + "conf" + File.separator;
    /**
     * 静态文件路径
     */
    String PATH_WEB = PATH_WORK + "web" + File.separator;
    /**
     * html文件路径
     */
    String PATH_PAGE = PATH_WEB + "page" + File.separator;
    /**
     * 下载路径
     */
    String PATH_DOWNLOAD = ConfigurationHelper.getString("download_path") + File.separator;


    // ==========================================  请求地址相关 ==========================================================

    /**
     * 下载请求路径
     */
    String URL_DOWNLOAD = "/download/";
    /**
     * 订阅服务地址
     */
    String URL_SUBSCRIBE = "/subscribe/add.json";
    /**
     * 取消订阅服务地址
     */
    String URL_UN_SUBSCRIBE = "/subscribe/cancel.json";
    /**
     * 版本发布接口
     */
    String URL_PUBLISH = "/version/add.json";

    // ==========================================  后台任务相关 ==========================================================

    /**
     * 任务链
     */
    String TASK_CHAIN = "chainTask";
    /**
     * 任务链-任务名称
     */
    String TASK_CHAIN_NAMES = "TASK_chainTaskNames";
    /**
     * 任务链-任务名称
     */
    String TASK_CHAIN_DELAY = "TASK_chainTaskDelay";
    /**
     * 订阅后台任务
     */
    String TASK_SUBSCRIBE = "subscribeTask";
    /**
     * 取消订阅后台任务
     */
    String TASK_UN_SUBSCRIBE = "unSubscribeTask";
    /**
     * 发布后台任务
     */
    String TASK_PUBLISH = "publishTask";
    /**
     * 部署后台任务
     */
    String TASK_DEPLOY = "deployTask";


    // ==========================================  请求头相关 ============================================================

    String CONTENT_TYPE_PLAIN = "text/plain";
    String CONTENT_TYPE_FILE = "application/octet-stream";
    String CONTENT_TYPE_HTML = "text/html";
    String CONTENT_TYPE_JS = "application/javascript;charset=UTF-8";
    String CONTENT_TYPE_CSS = "text/css";
    String CONTENT_TYPE_GIF = "image/gif";
    String CONTENT_TYPE_JPG = "image/jpeg";
    String CONTENT_TYPE_JSON = "application/json";


    // ==========================================  http请求相关 ==========================================================

    String HTTP_SCHEMA_HTTP = "http://";
    String HTTP_METHOD = "HTTP_METHOD";
    String HTTP_METHOD_GET = "GET";
    String HTTP_METHOD_POST = "POST";
    String HTTP_URL = "HTTP_URL";
    String HTTP_MESSAGE = "HTTP_MESSAGE";
}
