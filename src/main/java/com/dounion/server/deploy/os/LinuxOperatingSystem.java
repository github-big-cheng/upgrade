package com.dounion.server.deploy.os;

/**
 * Linux 操作系统
 */
public class LinuxOperatingSystem implements OperatingSystem {

    @Override
    public String getScriptPackage() {
        return "linux";
    }

    @Override
    public String[] getDefaultEnvironmentCmd(){
        return new String[]{"/bin/sh", "-c"};
    }
}
