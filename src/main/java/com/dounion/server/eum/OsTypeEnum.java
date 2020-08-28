package com.dounion.server.eum;

import java.util.HashMap;
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

    @Override
    public String toString() {
        return this.code;
    }



    public static Set<String> getSet(){
        return getMap().keySet();
    }

    public static Map<String, OsTypeEnum> getMap(){
        if(map == null){
            map = new HashMap<>();
            for(OsTypeEnum eum : OsTypeEnum.values()){
                map.put(eum.code, eum);
            }
        }
        return map;
    }
}
