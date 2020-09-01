package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.core.task.annotation.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * 任务链后台任务
 */
@Task(Constant.TASK_CHAIN)
public class ChainTask extends BaseTask {

    private final Logger logger = LoggerFactory.getLogger(ChainTask.class);

    @Override
    public String getTaskName() {
        return "任务链后台任务";
    }

    @Override
    protected void execute() {
        try {
            Map<String, Object> params = super.getParams();

            String[] taskNames = (String[]) params.get(Constant.TASK_CHAIN_NAMES);
            if (taskNames == null || taskNames.length == 0) {
                logger.warn("task:[{}] taskNames check failed", this);
                return;
            }
            Integer delay = (Integer) params.get(Constant.TASK_CHAIN_DELAY);
            if (delay == null) {
                delay = 0;
            }

            for (String taskName : taskNames) {
                Future<Integer> task = TaskHandler.callTaskBlock(taskName, params, delay);
                Integer id = task.get();
                logger.debug("chain task:[{}] completed, sub task:[{}]", this, TaskHandler.getTask(id));
            }
        } catch (Exception e) {
            logger.error("chain task:[{}] error:{}", this, e);
        }
    }
}