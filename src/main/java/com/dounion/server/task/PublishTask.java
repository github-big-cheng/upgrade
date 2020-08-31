package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.task.annotation.Task;

/**
 * 更新通知后台任务
 */
@Task(Constant.TASK_PUBLISH)
public class PublishTask extends BaseTask {


    @Override
    public String getTaskName() {
        return "部署后台任务";
    }

    @Override
    protected void execute() {

        // called scripts here

    }
}
