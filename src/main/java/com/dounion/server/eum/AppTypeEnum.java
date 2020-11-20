package com.dounion.server.eum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务类型
 */
public enum AppTypeEnum {

    // tomcat
    APP("40100-1001", "app", "app.war", DeployTypeEnum.TOMCAT),
    PUSH("40100-1002", "push", "push.war", DeployTypeEnum.TOMCAT),
    APP_SERVER("40100-1003", "appServer", "appServer.war", DeployTypeEnum.TOMCAT),
    BIZ_SERVICE("40100-1004", "bizService", "bizService.war", DeployTypeEnum.TOMCAT),
    DO_BPM("40100-1005", "dobpm", "dobpm.war", DeployTypeEnum.TOMCAT),
    DO_SMS_SERVER("40100-1006", "dosmsserver", "dosmsserver.war", DeployTypeEnum.TOMCAT),
    FILE("40100-1007", "file", "file.war", DeployTypeEnum.TOMCAT),
    REMIND("40100-1008", "remind", "remind.war", DeployTypeEnum.TOMCAT),
    DO_REMIND("40100-1009", "doremind", "doremind.war", DeployTypeEnum.TOMCAT),
    GTS("40100-1010", "gts", "gts.war", DeployTypeEnum.TOMCAT),
    DO_OA("40100-1011", "dooa", "dooa.war", DeployTypeEnum.TOMCAT),
    DO_HR("40100-1012", "dohr", "dohr.war", DeployTypeEnum.TOMCAT),
    NBS("40100-1013", "nbs", "nbs.war", DeployTypeEnum.TOMCAT),

    // main
    DO_SMART_MASTER("40100-2001", "dosmart-master", "dosmart-master.zip", DeployTypeEnum.MAIN),
    DO_SMART_TASK("40100-2002", "dosmart-task", "dosmart-task.zip", DeployTypeEnum.MAIN),
    DO_SMART_LOG("40100-2003", "dosmart-log", "dosmart-log.zip", DeployTypeEnum.MAIN),
    DO_SMART_CONTROLLER("40100-2004", "dosmart-controller-1.0.0", "dosmart-controller-1.0.0.zip", DeployTypeEnum.MAIN),
    GTS_CONTROLLER("40100-2005", "gtsController", "gtsController.rar", DeployTypeEnum.MAIN),
    PRINT("40100-2006", "print", "print.zip", DeployTypeEnum.MAIN),
    UPGRADE("40100-2007", "upgrade", "upgrade.zip", DeployTypeEnum.MAIN),

    // 下载文件
    GRAIN_ANDROID_APK("40100-3001", "grain-android", "grain-android.apk", DeployTypeEnum.FILE),
    DO_SMART_SCREEN_APK("40100-3002", "grain-screen", "grain-screen.apk", DeployTypeEnum.FILE),
    DO_SMART_PAD_APK("40100-3003", "grain-pad", "grain-pad.apk", DeployTypeEnum.FILE),
    DOUNION_VIDEO("40100-3004", "dounion_video", "dounion_video.msi", DeployTypeEnum.FILE),
    DOUNION_VIDEO_NET("40100-3005", "dotNetFx45_Full_setup", "dotNetFx45_Full_setup.exe", DeployTypeEnum.FILE),
    D_TOOLS("40100-3006", "dtools", "dtools.exe", DeployTypeEnum.FILE), // 打印控件

    // 数据库
    MY_SQL("40100-4001", "mysql", "mysql", DeployTypeEnum.MYSQL),

    // 配置文件相关
    PROPERTIES_APP("40100-5101", "app", "framework.properties", DeployTypeEnum.PROPERTIES, APP),
    PROPERTIES_PUSH("40100-5102", "push", "config_push.properties", DeployTypeEnum.PROPERTIES, PUSH),
    PROPERTIES_APP_SERVER("40100-5103", "appServer", "config_appServer.properties", DeployTypeEnum.PROPERTIES, APP_SERVER),
    PROPERTIES_BIZ_SERVICE("40100-5104", "bizService", "config_dograin_service.properties", DeployTypeEnum.PROPERTIES, BIZ_SERVICE),
    PROPERTIES_DO_SMS_SERVER("40100-5106", "dosmsserver", "config_dosmsserver.properties", DeployTypeEnum.PROPERTIES, DO_SMS_SERVER),
    PROPERTIES_FILE("40100-5107", "file", "config_file.properties", DeployTypeEnum.PROPERTIES, FILE),
    PROPERTIES_REMIND("40100-5108", "remind", "config_doremind.properties", DeployTypeEnum.PROPERTIES, REMIND),
    PROPERTIES_DO_REMIND("40100-5109", "doremind", "config_doremind.properties", DeployTypeEnum.PROPERTIES, DO_REMIND),
    PROPERTIES_GTS("40100-5110", "gts", "config_dograin_gts.properties", DeployTypeEnum.PROPERTIES, GTS),
    PROPERTIES_DO_OA("40100-5111", "dooa", "config_dograin_oa.properties", DeployTypeEnum.PROPERTIES, DO_OA),
    PROPERTIES_DO_HR("40100-5112", "dohr", "config_dograin_hr.properties", DeployTypeEnum.PROPERTIES, DO_HR),
    PROPERTIES_NBS("40100-5113", "nbs", "config_dograin_nbs.properties", DeployTypeEnum.PROPERTIES, NBS),

    PROPERTIES_DO_SMART_MASTER("40100-5201", "dosmart-master", "config_master.properties", DeployTypeEnum.PROPERTIES, DO_SMART_MASTER),
    PROPERTIES_DO_SMART_TASK("40100-5202", "dosmart-task", "config_task.properties", DeployTypeEnum.PROPERTIES, DO_SMART_TASK),
    PROPERTIES_DO_SMART_LOG("40100-5203", "dosmart-log", "config_log.properties", DeployTypeEnum.PROPERTIES, DO_SMART_LOG),
    PROPERTIES_DO_SMART_CONTROLLER("40100-5204", "dosmart-controller", "config_controller.properties", DeployTypeEnum.PROPERTIES, DO_SMART_CONTROLLER),
    PROPERTIES_GTS_CONTROLLER("40100-5205", "gtsController", "application-dev.properties", DeployTypeEnum.PROPERTIES, GTS_CONTROLLER),
    PROPERTIES_PRINT("40100-5206", "print", "system.properties", DeployTypeEnum.PROPERTIES, PRINT),
    PROPERTIES_UPGRADE("40100-5207", "upgrade", "config_upgrade.properties", DeployTypeEnum.PROPERTIES, UPGRADE),

    // spring-boot
    DOUNION_SSO("40100-6001", "sso_web", "sso_web.jar", DeployTypeEnum.SPRING_BOOT),
    DOUNION_UCENTER("40100-6002", "ucenter_web", "ucenter_web.jar", DeployTypeEnum.SPRING_BOOT),
    DOUNION_OA("40100-6003", "oa_web", "oa_web.jar", DeployTypeEnum.SPRING_BOOT),
    DOUNION_ACTIVITI("40100-6004", "activiti_web", "activiti_web.jar", DeployTypeEnum.SPRING_BOOT),

    // static-file
    STATIC_OA("40100-7001", "static", "static.zip", DeployTypeEnum.STATIC_FILE),
    ;


    AppTypeEnum(String code, String appName, String fileName, DeployTypeEnum deployType) {
        this.code = code;
        this.appName = appName;
        this.fileName = fileName;
        this.deployType = deployType;
    }

    AppTypeEnum(String code, String appName, String fileName, DeployTypeEnum deployType, AppTypeEnum parent) {
        this.code = code;
        this.appName = appName;
        this.fileName = fileName;
        this.deployType = deployType;
        this.parent = parent;
    }

    private String code;
    private String appName;
    private String fileName;
    private DeployTypeEnum deployType;
    private AppTypeEnum parent;
    private static Map<String, AppTypeEnum> map;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DeployTypeEnum getDeployType() {
        return deployType;
    }

    public void setDeployType(DeployTypeEnum deployType) {
        this.deployType = deployType;
    }

    public AppTypeEnum getParent() {
        return parent;
    }

    public void setParent(AppTypeEnum parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return this.code + "-" + this.fileName;
    }

    public boolean equals(AppTypeEnum enu){
        return this.code.equals(enu.code);
    }


    public static Set<String> getSet(){
        return getMap().keySet();
    }

    public static Map<String, AppTypeEnum> getMap(){
        if(map == null){
            map = new LinkedHashMap<>();
            for(AppTypeEnum eum : AppTypeEnum.values()){
                map.put(eum.code, eum);
            }
        }
        return map;
    }
    
}
