package com.dounion.server.deploy.os;

public interface OperatingSystem {

    String getScriptPackage();

    String[] getDefaultEnvironmentCmd();

}
