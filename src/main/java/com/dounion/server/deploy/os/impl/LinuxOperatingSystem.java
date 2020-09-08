package com.dounion.server.deploy.os.impl;

import com.dounion.server.deploy.os.OperatingSystem;

/**
 * Linux 操作系统
 */
public class LinuxOperatingSystem implements OperatingSystem {

    @Override
    public String getScriptCallMethod() {
        return "sh";
    }

    @Override
    public String getScriptSuffix() {
        return ".sh";
    }

    @Override
    public String getScriptPackage() {
        return "linux/";
    }

    @Override
    public String[] getDefaultEnvironmentCmd(){
        return new String[]{"/bin/bash", "-c"};
    }
}
