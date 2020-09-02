package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.task.annotation.Task;

@Task("testTask")
public class TestTask extends BaseTask {

    @Override
    public String getTaskName() {
        return "这是测试任务";
    }

    @Override
    public void execute() throws Exception {

        try {
            Thread.sleep(10000);

            if(super.isInterrupted()){
                System.out.println("我被中断了:" + this);
                return;
            }

            System.out.println("我被执行了:" + this);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return;
    }
}
