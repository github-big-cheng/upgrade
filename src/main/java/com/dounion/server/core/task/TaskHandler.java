package com.dounion.server.core.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.helper.SpringApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
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
    public static BlockingQueue<BaseTask> TASK_QUEUE = new LinkedBlockingQueue<>();
    public static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);
    private static AtomicInteger TASK_ID = new AtomicInteger(0);
    private static ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

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

                EXECUTOR_SERVICE.submit(task);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 调用后台任务
     * @param taskName
     * @return
     */
    public static Integer callTask(String taskName) {
        Integer id =  TASK_ID.addAndGet(1);

        BaseTask task = SpringApp.getInstance().getBean(taskName, BaseTask.class);
        task.setTaskId(id);
        TASK_QUEUE.add(task);

        return id;
    }

}
