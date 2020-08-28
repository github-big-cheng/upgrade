package com.dounion.server.controller;

import com.dounion.server.core.request.ResponseBuilder;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.eum.OsTypeEnum;
import com.dounion.server.eum.ResponseTypeEnum;
import com.dounion.server.eum.ServiceTypeEnum;
import com.dounion.server.eum.UpgradeTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/common")
public class CommonController {

    private final static Logger logger = LoggerFactory.getLogger(CommonController.class);

    final static Map<String, Object> DICT_MAP = new HashMap(){{
        put("serviceType", ServiceTypeEnum.getMap());
        put("osType", OsTypeEnum.getMap());
        put("upgradeType", UpgradeTypeEnum.getMap());
    }};

    /**
     * 字典查询
     * @param type
     * @return
     */
    @RequestMapping(name = "字典查询", value = "/dictList.json")
    @ResponseType(ResponseTypeEnum.JSON)
    public Object dictList(String type){
        if(StringUtils.isBlank(type)){
            return ResponseBuilder.buildSuccess(DICT_MAP);
        }

        return ResponseBuilder.buildSuccess(DICT_MAP.get(type));
    }

}
