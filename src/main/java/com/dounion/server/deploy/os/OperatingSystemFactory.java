package com.dounion.server.deploy.os;


/**
 * 操作系统工厂类
 */
public class OperatingSystemFactory {

    final static String OS_TYPE = System.getProperty("os.name");

    public static OperatingSystem build(){
        if(OS_TYPE.toLowerCase().startsWith("win")){
            return new WindowsOperatingSystem();
        }

        return new LinuxOperatingSystem();
    }

}
