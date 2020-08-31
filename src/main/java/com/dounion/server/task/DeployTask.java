package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.task.annotation.Task;

/**
 * 部署后台任务
 */
@Task(Constant.TASk_DEPLOY)
public class DeployTask extends BaseTask {


    @Override
    public String getTaskName() {
        return "部署后台任务";
    }

    @Override
    protected void execute() {

        // some logic about do upgrade

        // called scripts here

    }
}
