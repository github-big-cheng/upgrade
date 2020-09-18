package com.dounion.server.eum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 操作系统类型
 */
public enum OsTypeEnum {

    LINUX("40300-10", "Linux"),
    WINDOWS("40300-20", "Windows"),
    ANDROID("40300-30", "Android")
    ;

    OsTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
    private static Map<String, OsTypeEnum> map;


    public boolean equals(OsTypeEnum enu){
        return this.code.equals(enu.code);
    }

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

    @Override
    public String toString() {
        return this.code + "-" + this.desc;
    }



    public static Set<String> getSet(){
        return getMap().keySet();
    }

    public static Map<String, OsTypeEnum> getMap(){
        if(map == null){
            map = new LinkedHashMap<>();
            for(OsTypeEnum eum : OsTypeEnum.values()){
                map.put(eum.code, eum);
            }
        }
        return map;
    }
}
