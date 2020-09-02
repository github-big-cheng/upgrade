package com.dounion.server.service;

import com.dounion.server.entity.UpgradeRecord;
import com.dounion.server.entity.VersionInfo;

import java.util.List;
import java.util.Map;

public interface UpgradeRecordService {

    /**
     * 发布列表查询
     * @return
     */
    List<Map<String, Object>> publishListQuery(Map<String, Object> params);

    List<UpgradeRecord> selectListBySelective(UpgradeRecord record);

    int insert(UpgradeRecord record);

    int updateBySelective(UpgradeRecord record);

    List<UpgradeRecord> selectEntityListBySelective(UpgradeRecord query);

    void upgradeRecordsGenerate(VersionInfo versionInfo, String subscribeCodes);
}
