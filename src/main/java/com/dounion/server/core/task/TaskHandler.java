package com.dounion.server.core.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 后台任务处理器
 */
public class TaskHandler implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(TaskHandler.class);

    // 私有化构造方法
    private TaskHandler(){

    }

    // 任务处理队列
    private static BlockingQueue<BaseTask> TASK_QUEUE = new LinkedBlockingQueue<>();
    // 线程池
    public static ExecutorService EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(ConfigurationHelper.getInt(Constant.CONF_TASK_MAX_THREAD_COUNT, 5));
    // 任务ID生成器
    private static AtomicInteger TASK_ID = new AtomicInteger(0);
    // 任务控制集合
    private final static ConcurrentHashMap<Integer, ThreadLocal<BaseTask>> THREAD_LOCAL_MAP = new ConcurrentHashMap<>();
    // 循环执行任务单例控制
    public final static ConcurrentHashMap<String, BaseTask> LOOP_TASK_MAP = new ConcurrentHashMap<>();

    static {
        // 创建后台任务处理器
        EXECUTOR_SERVICE.submit(new TaskHandler());
    }


    /**
     * 后台任务-主线程
     */
    @Override
    public void run() {
        try {
            while (true) {
                // 阻塞获取任务
                final BaseTask task = TASK_QUEUE.take();
                if(task == null){
                    logger.warn("task 【{}】 not config,please check it", task);
                    continue;
                }

                // 定时任务处理
                if(task.isLoop()){
                    // 保证loop task 单例运行
                    BaseTask temp = LOOP_TASK_MAP.get(task.getTaskName());
                    if(temp!=null && temp.isActive()){
                        continue;
                    }
                    LOOP_TASK_MAP.put(task.getTaskName(), task);
                }

                Future future = EXECUTOR_SERVICE.submit(task);
                task.setFuture(future);
            }
        } catch (Exception e) {
            logger.error("TaskHandler run error:{}", e);
        }
    }

    // ===============================================  BaseTask对象处理  ================================================


    /**
     *
     * @return
     */
    public static BaseTask taskInit(BaseTask task, ConcurrentHashMap<String, Object> params){

        if(task == null){
            return null;
        }

        final BaseTask taskF = task;

        // 生成任务ID
        Integer id = TASK_ID.addAndGet(1);
        taskF.setTaskId(id);
        // 运行参数处理
        if(params == null){
            params = new ConcurrentHashMap<>();
        }
        if(taskF.getParams() != null){
            taskF.getParams().putAll(params);
        } else {
            taskF.setParams(params);
        }

        // 设置ThreadLocal 清理
        task.setCallback(new BaseTask.Callback() {
            @Override
            public void doSomething() {
                // 任务结束，移除对应的任务线程变量
                ThreadLocal<BaseTask> threadLocal = THREAD_LOCAL_MAP.get(taskF.getTaskId());
                BaseTask task1 = threadLocal.get();
                if(task1 !=null && !taskF.isLoop()){
                    threadLocal.remove();
                    THREAD_LOCAL_MAP.remove(taskF.getTaskId());
                }
            }
        });


        return taskF;
    }


    // ===============================================  任务调度  ========================================================


    /**
     * 调用后台任务
     * @param task
     * @param params 额外参数
     * @param delay 延迟多少秒提交
     * @return
     */
    public static Integer callTask(BaseTask task, ConcurrentHashMap<String, Object> params, final long delay) {

        if(task == null){
            logger.warn("task 【{}】 not config, please check it...");
            return null;
        }

        final BaseTask taskF = taskInit(task, params);

        THREAD_LOCAL_MAP.put(taskF.getTaskId(), new ThreadLocal<BaseTask>(){
            @Override
            protected BaseTask initialValue() {
                return taskF;
            }
        });

        Future future = EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TASK_QUEUE.add(taskF); // 通过队列添加的方式，可以添加task到任务管理中
            }
        });
        taskF.setFuture(future);

        return taskF.getTaskId();
    }


    /**
     * 调用后台任务
     * @param taskName
     * @param params 额外参数
     * @param delay 延迟多少秒提交
     * @return
     */
    public static Integer callTask(String taskName, ConcurrentHashMap<String, Object> params, final long delay) {
        final BaseTask task = SpringApp.getInstance().getBean(taskName, BaseTask.class);
        return callTask(task, params, delay);
    }



    /**
     * 调用后台任务
     * @param task 任务名称
     * @param delay 延迟时间
     * @return
     */
    public static Integer callTask(BaseTask task, long delay) {
        return callTask(task, task.getParams(), delay);
    }




    /**
     * 调用后台任务
     * @param taskName 任务名称
     * @param delay 延迟时间
     * @return
     */
    public static Integer callTask(String taskName, long delay) {
        return callTask(taskName, null, delay);
    }


    /**
     * 调用后台任务
     * @param task 任务
     * @param params 额外参数
     * @return
     */
    public static Integer callTask(BaseTask task, ConcurrentHashMap<String, Object> params) {
        return callTask(task, params, 0);
    }


    /**
     * 调用后台任务
     * @param taskName 任务名称
     * @param params 额外参数
     * @return
     */
    public static Integer callTask(String taskName, ConcurrentHashMap<String, Object> params) {
        return callTask(taskName, params, 0);
    }


    /**
     * 调用后台任务
     * @param task
     * @return
     */
    public static Integer callTask(BaseTask task) {
        return callTask(task, task.getParams());
    }


    /**
     * 调用后台任务
     * @param taskName 任务名称
     * @return
     */
    public static Integer callTask(String taskName) {
        return callTask(taskName, null);
    }


    // ===============================================  阻塞任务  ========================================================



    /**
     * 获取Future调用后台任务
     *      use future.get() to block it
     * @param task
     * @param params 额外参数
     * @param delay 延迟多少秒提交
     * @return
     */
    public static Future callTaskBlock(BaseTask task, ConcurrentHashMap<String, Object> params, final long delay) {

        if(task == null){
            logger.warn("task 【{}】 not config, please check it...");
            return null;
        }

        final BaseTask taskF = taskInit(task, params);

        THREAD_LOCAL_MAP.put(taskF.getTaskId(), new ThreadLocal<BaseTask>(){
            @Override
            protected BaseTask initialValue() {
                return taskF;
            }
        });

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Future future = EXECUTOR_SERVICE.submit(taskF);
        taskF.setFuture(future);
        return taskF.getFuture();
    }


    /**
     * 获取Future调用后台任务
     *      use future.get() to block it
     * @param taskName
     * @param params 额外参数
     * @param delay 延迟多少秒提交
     * @return
     */
    public static Future callTaskBlock(String taskName, ConcurrentHashMap<String, Object> params, final long delay) {
        final BaseTask task = SpringApp.getInstance().getBean(taskName, BaseTask.class);
        return callTaskBlock(task, params, delay);
    }


    // ===============================================  任务链  =========================================================

    /**
     * 任务链
     * @param params
     * @param delay
     *          The <code>delay</code> not only delay the chain task submit times now,
     *          it also delay every task's interval times.
     * @param tasks
     */
    public static Integer callTaskChain(ConcurrentHashMap<String, Object> params, final long delay, BaseTask... tasks) {
        if(tasks==null || tasks.length==0){
            logger.warn("task chain submit failed, taskNames must not be empty");
            return null;
        }
        if(tasks.length == 1){
            logger.warn("call TaskHandler.callTask() will be much more quickly then this one");
        }

        if(params == null){
            params = new ConcurrentHashMap<>();
        }
        params.put(Constant.TASK_CHAIN_NAMES, CollectionUtils.arrayToList(tasks));
        params.put(Constant.TASK_CHAIN_DELAY, delay);

        return callTask(Constant.TASK_CHAIN, params, delay);
    }


    /**
     * 任务链
     * @param params
     * @param delay
     *          The <code>delay</code> not only delay the chain task submit times now,
     *          it also delay every task's interval times.
     * @param taskNames
     */
    public static Integer callTaskChain(ConcurrentHashMap<String, Object> params, final long delay, String... taskNames) {
        if(taskNames==null || taskNames.length==0){
            logger.warn("task chain submit failed, taskNames must not be empty");
            return null;
        }
        if(taskNames.length == 1){
            logger.warn("call TaskHandler.callTask() will be much more quickly then this one");
        }

        if(params == null){
            params = new ConcurrentHashMap<>();
        }

        BaseTask[] taskArr = new BaseTask[taskNames.length];
        BaseTask task;
        for(int i=0; i<taskNames.length; i++){
            task = SpringApp.getInstance().getBean(taskNames[i]);
            if(task == null){
                throw new SystemException(StringHelper.parse1("bean 【{}】 not found", taskNames[i]));
            }
            taskArr[i] = (task);
        }

        params.put(Constant.TASK_CHAIN_NAMES, taskNames);
        params.put(Constant.TASK_CHAIN_DELAY, delay);

        return callTaskChain(params, delay, taskArr);
    }



    /**
     * 任务链
     * @param params
     * @param taskNames
     */
    public static Integer callTaskChain(ConcurrentHashMap<String, Object> params, String... taskNames) {
        return callTaskChain(params, 1000l, taskNames);
    }

    /**
     * 任务链
     * @param taskNames
     */
    public static Integer callTaskChain(String... taskNames) {
        return callTaskChain(null, 1000l, taskNames);
    }



    /**
     * 获取具体任务对象，可能已执行完毕被删除
     */
    public static BaseTask getTask(Integer id) {
        if(id == null){
            return null;
        }
        return THREAD_LOCAL_MAP.get(id)==null ? null : THREAD_LOCAL_MAP.get(id).get();
    }


    /**
     * 提交任务中断请求
     * @param taskId
     * @return
     */
    public static boolean interrupted(Integer taskId){
        BaseTask task = getTask(taskId);
        return task==null ? false : task.interrupted();
    }


    /**
     * 获取当前任务列表
     * @return
     */
    public static List<BaseTask> getTaskList(){
        List<BaseTask> tasks = new ArrayList<>();
        for(Integer id : THREAD_LOCAL_MAP.keySet()){
            tasks.add(THREAD_LOCAL_MAP.get(id).get());
        }
        return tasks;
    }


    /**
     * 获取定时任务列表
     * @return
     */
    public static List<BaseTask> getLoopTaskList(){
        List<BaseTask> tasks = new ArrayList<>();
        for(String name : LOOP_TASK_MAP.keySet()){
            tasks.add(LOOP_TASK_MAP.get(name));
        }
        return tasks;
    }


    /**
     * 按名称唤醒sleep中的线程
     *  Thread.sleep方法需要被 try catch
     *      try {
     *          Thread.sleep(delay);
     *      } catch (InterruptedException e) {
     *          e.printStackTrace();
     *      }
     * @param taskName
     * @return
     */
    public static boolean wakeUp(String taskName){
        BaseTask task = LOOP_TASK_MAP.get(taskName);
        if(task == null){
            return false;
        }

        return task.wakeUp();
    }

}
