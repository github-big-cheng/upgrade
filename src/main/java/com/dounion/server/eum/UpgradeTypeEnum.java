package com.dounion.server.eum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 更新操作类型
 */
public enum UpgradeTypeEnum {

    TOMCAT("40700-10", "Tomcat"),
    MAIN("40700-20", "Main"),
    FILE("40700-30", "file")
    ;

    UpgradeTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
    private static Map<String, UpgradeTypeEnum> map;

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

    public boolean equals(UpgradeTypeEnum enu){
        return this.code.equals(enu.code);
    }


    public static Set<String> getSet(){
        return getMap().keySet();
    }

    public static Map<String, UpgradeTypeEnum> getMap(){
        if(map == null){
            map = new HashMap<>();
            for(UpgradeTypeEnum eum : UpgradeTypeEnum.values()){
                map.put(eum.code, eum);
            }
        }
        return map;
    }

}
