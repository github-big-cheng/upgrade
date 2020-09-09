package com.dounion.server.eum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务类型
 */
public enum AppTypeEnum {

    APP("40100-140", "app", DeployTypeEnum.TOMCAT),
    DO_BPM("40100-150", "dobpm", DeployTypeEnum.TOMCAT),
    DO_FILE("40100-220", "dofile", DeployTypeEnum.TOMCAT),
    APP_SERVER("40100-10", "dosmart-appServer", DeployTypeEnum.TOMCAT),
    DO_SMART_CONTROLLER("40100-60", "dosmart-controller", DeployTypeEnum.MAIN),
    DO_SMART_LOG("40100-40", "dosmart-log", DeployTypeEnum.MAIN),
    DO_SMART_MASTER("40100-20", "dosmart-master", DeployTypeEnum.MAIN),
    DO_SMART_PAD_APK("40100-70", "dosmart-pad-apk", DeployTypeEnum.FILE),
    DO_SMART_PHONE_APK("40100-80", "dosmart-phone-apk", DeployTypeEnum.FILE),
    DO_SMART_SCREEN_APK("40100-90", "dosmart-screen-apk", DeployTypeEnum.FILE),
    DO_SMART_TASK("40100-30", "dosmart-task", DeployTypeEnum.MAIN),
    DO_SMART_VIDEO("40100-50", "dosmart-video", DeployTypeEnum.FILE),
    DO_SMS_SERVER("40100-230", "dosmsserver", DeployTypeEnum.TOMCAT),
    GRAIN_PHONE_APK("40100-130", "grain-phone-apk", DeployTypeEnum.FILE),
    GTS("40100-100", "gts", DeployTypeEnum.TOMCAT),
//    GTS_APP("40100-110", "gts-app", null),
    GTS_CONTROLLER("40100-120", "gts-controller", DeployTypeEnum.MAIN),
//    HR_APP("40100-190", "hr_app", null),
    IDOC("40100-290", "idoc", DeployTypeEnum.MAIN),
//    OA_APP("40100-180", "oa_app", null),
//    OFFICEUTIL("40100-250", "officeutil", null),
//    PAMS_APP("40100-200", "pams_app", null),
//    PORTAL_APP("40100-160", "portal_app", null),
    PRINT("40100-210", "print", DeployTypeEnum.MAIN),
//    RED("40100-300", "red", null),
    REMIND("40100-260", "remind", DeployTypeEnum.TOMCAT),
    SEND("40100-240", "send", DeployTypeEnum.TOMCAT),
//    TRAIN_APP("40100-170", "train_app", null),
//    upgrade_linux("40100-280", "upgrade-linux"),
//    upgrade-win("40100-270", "upgrade-win")
    UPGRADE("40100-270", "upgrade", DeployTypeEnum.MAIN)
    ;


    AppTypeEnum(String code, String desc, DeployTypeEnum deployType) {
        this.code = code;
        this.desc = desc;
        this.deployType = deployType;
    }

    private String code;
    private String desc;
    private DeployTypeEnum deployType;
    private static Map<String, AppTypeEnum> map;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DeployTypeEnum getDeployType() {
        return deployType;
    }

    public void setDeployType(DeployTypeEnum deployType) {
        this.deployType = deployType;
    }

    @Override
    public String toString() {
        return this.code + "-" + this.desc;
    }

    public boolean equals(AppTypeEnum enu){
        return this.code.equals(enu.code);
    }


    public static Set<String> getSet(){
        return getMap().keySet();
    }

    public static Map<String, AppTypeEnum> getMap(){
        if(map == null){
            map = new HashMap<>();
            for(AppTypeEnum eum : AppTypeEnum.values()){
                map.put(eum.code, eum);
            }
        }
        return map;
    }
    
}
