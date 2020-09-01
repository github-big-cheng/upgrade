package com.dounion.server.core.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.helper.SpringApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);
    private static AtomicInteger TASK_ID = new AtomicInteger(0);
    private static Map<Integer, ThreadLocal<BaseTask>> THREAD_LOCAL_MAP = new ConcurrentHashMap<>();

    static {
        new Thread(new TaskHandler()).start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 阻塞获取任务
                BaseTask task = TASK_QUEUE.take();
                if(task == null){
                    logger.warn("task [{}] not config,please check it", task);
                    continue;
                }

                Future id = EXECUTOR_SERVICE.submit(task);
                // 任务结束，移除对应的任务线程变量
                ThreadLocal<BaseTask> threadLocal = THREAD_LOCAL_MAP.get(id);
                threadLocal.remove();
                THREAD_LOCAL_MAP.remove(id);
            }
        } catch (Exception e) {
            logger.error("TaskHandler run error:{}", e);
        }
    }



    /**
     * 调用后台任务
     * @param taskName
     * @param params 额外参数
     * @param delay 延迟多少秒提交
     * @return
     */
    public static Integer callTask(String taskName, Map<String, Object> params, final long delay) {

        final BaseTask task = SpringApp.getInstance().getBean(taskName, BaseTask.class);
        if(task == null){
            logger.warn("task [{}] not config, please check it...");
            return null;
        }

        Integer id =  TASK_ID.addAndGet(1);
        task.setTaskId(id);
        task.setParams(params);
        THREAD_LOCAL_MAP.put(id, new ThreadLocal<BaseTask>(){
            @Override
            protected BaseTask initialValue() {
                return task;
            }
        });


        EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TASK_QUEUE.add(task);
            }
        });

        return id;
    }


    /**
     * 调用后台任务
     * @param taskName 任务名称
     * @param params 额外参数
     * @return
     */
    public static Integer callTask(String taskName, Map<String, Object> params) {
        return callTask(taskName, params, 0);
    }

    /**
     * 调用后台任务
     * @param taskName 任务名称
     * @return
     */
    public static Integer callTask(String taskName) {
        return callTask(taskName, null);
    }


    /**
     * 提交任务中断请求
     * @param taskId
     * @return
     */
    public static boolean interrupted(Integer taskId){
        ThreadLocal<BaseTask> taskThreadLocal = THREAD_LOCAL_MAP.get(taskId);
        return taskThreadLocal.get().interrupted();
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

}
