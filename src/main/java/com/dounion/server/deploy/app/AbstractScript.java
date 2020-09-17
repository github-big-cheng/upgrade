package com.dounion.server.deploy.app;

import com.dounion.server.core.base.AppInfo;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.deploy.DeployHandler;
import com.dounion.server.deploy.os.OperatingSystem;
import com.dounion.server.entity.VersionInfo;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class AbstractScript {

    protected Logger logger = LoggerFactory.getLogger(AbstractScript.class);

    protected ScriptParams params;

    protected OperatingSystem os;

    public void setOs(OperatingSystem os) {
        this.os = os;
    }

    public void setParams(ScriptParams params) {
        this.params = params;
    }

    public String getWorkDirectory() {
        return Constant.PATH_SCRIPT + os.getScriptPackage();
    }

    protected abstract String[] command();


    /**
     * 发布
     */
    public void deploy() {
        // 部署操作
        String[] cmd = (String[]) ArrayUtils.addAll(os.getDefaultEnvironmentCmd(), command());
        logger.debug("cmd... {}", Arrays.toString(cmd));
        DeployHandler.execute(this.getWorkDirectory(), cmd);
    }


    public static class ScriptParams {

        private AppInfo appInfo;
        private VersionInfo versionInfo;
        private String[] params; // 扩展参数

        public AppInfo getAppInfo() {
            return appInfo;
        }

        public void setAppInfo(AppInfo appInfo) {
            this.appInfo = appInfo;
        }

        public VersionInfo getVersionInfo() {
            return versionInfo;
        }

        public void setVersionInfo(VersionInfo versionInfo) {
            this.versionInfo = versionInfo;
        }

        public String[] getParams() {
            return params;
        }

        public void setParams(String[] params) {
            this.params = params;
        }
    }
}
