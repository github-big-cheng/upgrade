package com.dounion.server.controller;

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


    @RequestMapping(name = "任务列表", value = "/list")
    public String list(){
        return "task/list.html";
    }


    @RequestMapping("/list.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object listJson(){
        return ResponseBuilder.buildSuccess(TaskHandler.getTaskList());
    }


    @RequestMapping("/interrupted.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object interrupted(int taskId) {
        return ResponseBuilder.buildSuccess(TaskHandler.interrupted(taskId));
    }
}
