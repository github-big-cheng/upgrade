package com.dounion.server.eum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务类型
 */
public enum ServiceTypeEnum {

    APP("40100-140", "app"),
    DO_BPM("40100-150", "dobpm"),
    DO_FILE("40100-220", "dofile"),
    APP_SERVER("40100-10", "dosmart-appServer"),
    DO_SMART_CONTROLLER("40100-60", "dosmart-controller"),
    DO_SMART_LOG("40100-40", "dosmart-log"),
    DO_SMART_MASTER("40100-20", "dosmart-master"),
    DO_SMART_PAD_APK("40100-70", "dosmart-pad-apk"),
    DO_SMART_PHONE_APK("40100-80", "dosmart-phone-apk"),
    DO_SMART_SCREEN_APK("40100-90", "dosmart-screen-apk"),
    DO_SMART_TASK("40100-30", "dosmart-task"),
    DO_SMART_VIDEO("40100-50", "dosmart-video"),
    DO_SMS_SERVER("40100-230", "dosmsserver"),
    GRAIN_PHONE_APK("40100-130", "grain-phone-apk"),
    GTS("40100-100", "gts"),
    GTS_APP("40100-110", "gts-app"),
    GTS_CONTROLLER("40100-120", "gts-controller"),
    HR_APP("40100-190", "hr_app"),
    IDOC("40100-290", "idoc"),
    OA_APP("40100-180", "oa_app"),
    OFFICEUTIL("40100-250", "officeutil"),
    PAMS_APP("40100-200", "pams_app"),
    PORTAL_APP("40100-160", "portal_app"),
    PRINT("40100-210", "print"),
    RED("40100-300", "red"),
    REMIND("40100-260", "remind"),
    SEND("40100-240", "send"),
    TRAIN_APP("40100-170", "train_app"),
//    upgrade_linux("40100-280", "upgrade-linux"),
//    upgrade-win("40100-270", "upgrade-win")
    UPGRADE("40100-270", "upgrade")
    ;


    ServiceTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
    private static Map<String, ServiceTypeEnum> map;

    @Override
    public String toString() {
        return this.code;
    }

    public boolean equals(ServiceTypeEnum enu){
        return this.code.equals(enu.code);
    }


    public static Set<String> getSet(){
        return getMap().keySet();
    }

    public static Map<String, ServiceTypeEnum> getMap(){
        if(map == null){
            map = new HashMap<>();
            for(ServiceTypeEnum eum : ServiceTypeEnum.values()){
                map.put(eum.code, eum);
            }
        }
        return map;
    }
    
}
