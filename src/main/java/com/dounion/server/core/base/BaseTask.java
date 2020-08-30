package com.dounion.server.core.base;

/**
 * 抽象任务
 */
public abstract class BaseTask implements Runnable{


    protected Integer taskId;
    protected boolean interrupt = false;


    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public abstract String getTaskName();

    @Override
    public String toString() {
        return "task: [taskId=" + this.taskId + ", taskName=]" + this.getTaskName();
    }

    /**
     * 设置任务需要被中断标识
     *      仅仅设置标识，不表示一定可以中断，具体是否中断由具体任务判断标识处理
     */
    public void interrupted(){
        interrupt = true;
    }

    /**
     * 当前任务是否被要求中断
     * @return
     */
    protected boolean isInterrupted(){
        return interrupt;
    }


}
