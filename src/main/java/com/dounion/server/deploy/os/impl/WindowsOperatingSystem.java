package com.dounion.server.deploy.os.impl;

import com.dounion.server.deploy.os.OperatingSystem;

import java.io.File;

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
        return "windows" + File.separator;
    }

    @Override
    public String[] getDefaultEnvironmentCmd(){
        return new String[]{"cmd", "/c"};
    }
}
