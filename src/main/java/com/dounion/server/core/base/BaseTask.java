package com.dounion.server.core.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 抽象任务
 */
public abstract class BaseTask implements Callable<Integer> {

    protected final static Logger logger = LoggerFactory.getLogger(BaseTask.class);

    protected Integer taskId;
    protected Map<String, Object> params;
    private Callback callback;
    private boolean interrupt = false;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public abstract String getTaskName();

    @Override
    public String toString() {
        return "task: [taskId=" + this.taskId + ", taskName=" + this.getTaskName() + "]";
    }

    /**
     * 设置任务需要被中断标识
     *      仅仅设置标识，不表示一定可以中断，具体是否中断由具体任务判断标识处理
     */
    public boolean interrupted(){
        return interrupt = true;
    }

    /**
     * 当前任务是否被要求中断
     * @return
     */
    protected boolean isInterrupted(){
        return interrupt;
    }


    @Override
    public Integer call() throws Exception {

        // 任务开始前检查是否已被撤销
        if(this.isInterrupted()){
            logger.info("task 【{}】 has been interrupted, it will be exit...", this.taskId);
            return taskId;
        }

        try {
            // 调用实现类的执行方法
            this.execute();

            // 调用回调方法
            this.callback.doSomething();
        } catch (Exception e) {
            logger.error("task:【{}】执行异常:{}", this, e);
        }

        return this.taskId;
    }

    /**
     * 实际后台任务执行方法
     * @return
     */
    protected abstract void execute() throws Exception;

    public interface Callback{
        void doSomething();
    }

}
