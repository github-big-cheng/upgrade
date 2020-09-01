package com.dounion.server.service;

import com.dounion.server.entity.UpgradeRecord;

import java.util.List;
import java.util.Map;

public interface UpgradeRecordService {

    /**
     * 发布列表查询
     * @return
     */
    List<Map<String, Object>> publishListQuery();

    int insert(UpgradeRecord record);

    int updateBySelective(UpgradeRecord record);
}
