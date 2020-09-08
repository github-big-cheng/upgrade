package com.dounion.server.deploy.os.impl;

import com.dounion.server.deploy.os.OperatingSystem;

/**
 * Windows操作系统
 */
public class WindowsOperatingSystem implements OperatingSystem {

    @Override
    public String getScriptCallMethod() {
        return "";
    }

    @Override
    public String getScriptSuffix() {
        return ".bat";
    }

    @Override
    public String getScriptPackage() {
        return "windows/";
    }

    @Override
    public String[] getDefaultEnvironmentCmd(){
        return new String[]{"cmd", "/c"};
    }
}
