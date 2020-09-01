package com.dounion.server.service;

import java.util.List;
import java.util.Map;

public interface UpgradeRecordService {

    /**
     * 发布列表查询
     * @return
     */
    List<Map<String, Object>> publishListQuery();
}
