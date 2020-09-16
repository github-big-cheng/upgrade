package com.dounion.server.deploy.app;

import com.dounion.server.core.base.Constant;
import com.dounion.server.core.deploy.DeployHandler;
import com.dounion.server.deploy.os.OperatingSystem;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class AbstractScript {

    protected Logger logger = LoggerFactory.getLogger(AbstractScript.class);

    protected String[] params;

    protected OperatingSystem os;

    public void setOs(OperatingSystem os) {
        this.os = os;
    }

    public void setParams(String[] params) {
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
        String[] cmd = (String[]) ArrayUtils.addAll(os.getDefaultEnvironmentCmd(), command());
        logger.debug("cmd... {}", Arrays.toString(cmd));
        DeployHandler.execute(this.getWorkDirectory(), cmd);
    }
}
