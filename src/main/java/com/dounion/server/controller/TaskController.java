package com.dounion.server.controller;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.eum.ResponseTypeEnum;
import org.springframework.stereotype.Controller;

/**
 * 后台任务控制器
 */
@Controller
@RequestMapping("/task")
public class TaskController {


    /**
     * 列表页
     * @return
     */
    @RequestMapping(name = "任务列表", value = "/list")
    public String list(){
        return "task/list.html";
    }


    /**
     * 列表数据
     * @return
     */
    @RequestMapping("/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(){
        return ResponseBuilder.buildSuccess(TaskHandler.getTaskList());
    }


    /**
     * 发送中断指令
     * @param taskId
     * @return
     */
    @RequestMapping("/interrupted.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object interrupted(int taskId) {
        return ResponseBuilder.buildSuccess(TaskHandler.interrupted(taskId));
    }


    /**
     * 唤醒
     * @param taskId
     * @return
     */
    @RequestMapping("/wakeup.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object wakeup(int taskId) {
        BaseTask task = TaskHandler.getTask(taskId);
        boolean flag = TaskHandler.wakeUp(task.getTaskName());
        if(flag){
            return ResponseBuilder.buildSuccess();
        }
        return ResponseBuilder.buildError();
    }
}
