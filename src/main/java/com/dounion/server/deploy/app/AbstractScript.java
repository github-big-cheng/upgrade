package com.dounion.server.deploy.app;

import com.dounion.server.core.deploy.DeployHandler;
import com.dounion.server.deploy.os.OperatingSystem;
import org.apache.commons.lang.ArrayUtils;

public abstract class AbstractScript {

    protected String[] params;

    protected OperatingSystem os;

    public void setOs(OperatingSystem os) {
        this.os = os;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    protected abstract String[] command();


    /**
     * 发布
     */
    public void deploy() {
        String[] cmd = (String[]) ArrayUtils.addAll(os.getDefaultEnvironmentCmd(), command());
        DeployHandler.execute(cmd);
    }
}
