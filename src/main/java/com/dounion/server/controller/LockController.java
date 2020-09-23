package com.dounion.server.controller;

import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.core.task.LockHandler;
import com.dounion.server.eum.ResponseTypeEnum;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/lock")
@Controller
public class LockController {

    @RequestMapping("/info.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object infoJson(){
        Map<String, Object> map = new HashMap<>();

        map.put("lockMap", LockHandler.LOCK_HANDLER_MAP);

        return ResponseBuilder.buildSuccess(map);
    }

}
