package com.dounion.server.deploy.os;

public interface OperatingSystem {

    /**
     * 获取脚本调用方式
     * @return
     */
    String getScriptCallMethod();

    /**
     * 获取文件名后缀
     * @return
     */
    String getScriptSuffix();

    /**
     * 获取脚本路径
     * @return
     */
    String getScriptPackage();

    /**
     * 获取执行环境命令
     * @return
     */
    String[] getDefaultEnvironmentCmd();

}
