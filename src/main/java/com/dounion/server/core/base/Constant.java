package com.dounion.server.core.base;

import com.dounion.server.core.helper.ConfigurationHelper;

import java.io.File;

public interface Constant {

    // 临时文件夹
    String TEMP_PATH = System.getProperty("java.io.tmpdir");
    // 工作目录
//    String WORK_PATH = System.getProperty("user.dir") + File.separator;
    String WORK_PATH = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\";
    // 配置文件路径
    String CONG_PATH = WORK_PATH + "conf" + File.separator;
    // 静态文件路径
    String WEB_PATH = WORK_PATH + "web" + File.separator;
    // html文件路径
    String PAGE_PATH = WEB_PATH + "page" + File.separator;
    // 下载路径
    String DOWNLOAD_PATH = ConfigurationHelper.getString("download_path") + File.separator;

}
