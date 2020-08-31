package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.task.annotation.Task;

@Task("deployTask")
public class DeployTask extends BaseTask {


    @Override
    public String getTaskName() {
        return "部署后台任务";
    }

    @Override
    protected void execute() {


    }
}
