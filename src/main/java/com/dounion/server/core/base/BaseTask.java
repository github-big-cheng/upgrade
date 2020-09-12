package com.dounion.server.core.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.core.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 抽象任务
 */
public abstract class BaseTask implements Callable<Integer> {

    protected final static Logger logger = LoggerFactory.getLogger(BaseTask.class);

    protected Integer taskId; // 任务ID
    protected Map<String, Object> params; // 额外参数
    private Callback callback; // 回调函数
    private boolean interrupt = false; // 中断标识
    private Future<Integer> future;

    // 页面展示
    private String taskName;
    private String startTime;
    private String endTime;
    private long costTime;
    private BigDecimal progress = BigDecimal.ZERO; // 任务进度

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public long getCostTime() {
        return costTime;
    }

    public Future<Integer> getFuture() {
        return future;
    }

    public void setFuture(Future<Integer> future) {
        this.future = future;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    /**
     * progress 10%
     */
    protected void setProgressJustStart(){
        this.setProgress(BigDecimal.TEN);
    }

    /**
     * progress 25%
     */
    protected void setProgressTwentyFive(){
        this.setProgress(new BigDecimal(25));
    }

    /**
     * progress 50%
     */
    protected void setProgressHalf(){
        this.setProgress(new BigDecimal(50));
    }

    /**
     * progress 75%
     */
    protected void setProgressSeventyFive(){
        this.setProgress(new BigDecimal(75));
    }

    /**
     * progress 95%
     */
    protected void setProgressNeelyComplete(){
        this.setProgress(new BigDecimal(95));
    }

    /**
     * progress 100%
     */
    protected void setProgressComplete(){
        this.setProgress(new BigDecimal(100));
    }

    public abstract String getTaskName();

    public boolean isLoop() {
        return false;
    }

    public long getLoopDelay() {
        return 60 * 60 * 1000l; // 默认一个小时;
    }

    @Override
    public String toString() {
        return "task: " +
                "{taskId=" + this.taskId
                + ", taskName=" + this.getTaskName()
                + ", progress=" + this.getProgress()
                + ", isLoop=" + this.isLoop()
                + ", loopDelay=" + this.getLoopDelay()
                + ", params=" + StringHelper.jsonFormatString(this.params)
                + "}";
    }

    /**
     * 设置任务需要被中断标识
     *      仅仅设置标识，不表示一定可以中断，具体是否中断由具体任务判断标识处理
     */
    public boolean interrupted(){
        return interrupt = true;
    }


    /**
     * 唤醒sleep中的任务
     * @return
     */
    public boolean wakeUp(){
        return this.getFuture().cancel(true);
    }


    /**
     * 当前任务是否被要求中断
     * @return
     */
    protected boolean isInterrupted(){
        return interrupt;
    }

    public boolean isActive(){
        return Thread.currentThread().isAlive();
    }

    @Override
    public Integer call() throws Exception {

        long startTime = System.currentTimeMillis();
        // 设置开始时间
        this.setStartTime(DateHelper.format(new Date()));

        // 任务开始前检查是否已被撤销
        if(this.isInterrupted()){
            logger.info("task 【{}】 has been interrupted, it will be exit...", this.taskId);
            return taskId;
        }

        try {
            // 调用实现类的执行方法
            this.execute();

            // 快要哇
            this.setProgressNeelyComplete();

            // 调用回调方法
            if(this.callback != null){
                this.callback.doSomething();
            }

            // 设置完成
            this.setProgressComplete();

            // 设置结束时间
            this.setEndTime(DateHelper.format(new Date()));
            // 设置耗时
            this.setCostTime(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("【{}】执行异常:{}", this, e);
        }

        // 间隔定时任务 循环执行
        if(!this.isInterrupted() && this.isLoop()){
            Thread.sleep(this.getLoopDelay());
            this.call();
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
