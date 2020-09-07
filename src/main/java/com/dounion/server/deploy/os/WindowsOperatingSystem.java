package com.dounion.server.deploy.os;

/**
 * Windows操作系统
 */
public class WindowsOperatingSystem implements OperatingSystem {


    @Override
    public String getScriptPackage() {
        return "windows";
    }

    @Override
    public String[] getDefaultEnvironmentCmd(){
        return new String[]{"cmd", "/c"};
    }
}
