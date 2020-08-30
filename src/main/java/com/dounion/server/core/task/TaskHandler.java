package com.dounion.server.core.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.task.annotation.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.*;

import static com.dounion.server.core.helper.SpringApp.getInstance;

/**
 * 后台任务处理器
 */
public class TaskHandler implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(TaskHandler.class);

    // 私有化构造方法
    private TaskHandler(){

    }

    // 任务处理队列
    public static BlockingQueue<String> TASK_QUEUE = new LinkedBlockingQueue<>();
    public static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);

    static {
        new Thread(new TaskHandler()).start();
    }

    @Override
    public void run() {

        try {
            while (true) {
                // 阻塞获取任务
                String taskName = TASK_QUEUE.take();
                BaseTask task = SpringApp.getInstance().getBean(taskName, BaseTask.class);
                if(task == null){
                    logger.warn("task [{}] not config,please check it", taskName);
                    continue;
                }

                EXECUTOR_SERVICE.submit(task);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    public static void initialization(){
        Map<String, Object> map = getInstance().getObjectByAnnotationType(Task.class);
        logger.debug("taskHandler====>{}", map);
        for(String key : map.keySet()){
            Object o = map.get(key);
            System.out.println(o);
        }
    }

    public static void callTask(String taskName) {

    }

}
