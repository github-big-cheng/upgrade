package com.dounion.server.eum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 部署操作类型
 */
public enum DeployTypeEnum {

    RESTART("40700-00", "Restart"),
    TOMCAT("40700-10", "Tomcat"),
    MAIN("40700-20", "Main"),
    FILE("40700-30", "File"),
    MYSQL("40700-40", "MySql"),
    PROPERTIES("40700-50", "Properties"),
    ;

    DeployTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
    private static Map<String, DeployTypeEnum> map;

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

    public boolean equals(DeployTypeEnum enu){
        return this.code.equals(enu.code);
    }


    public static Set<String> getSet(){
        return getMap().keySet();
    }

    public static Map<String, DeployTypeEnum> getMap(){
        if(map == null){
            map = new LinkedHashMap<>();
            for(DeployTypeEnum eum : DeployTypeEnum.values()){
                map.put(eum.code, eum);
            }
        }
        return map;
    }

}
