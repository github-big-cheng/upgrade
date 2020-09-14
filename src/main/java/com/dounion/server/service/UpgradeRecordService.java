package com.dounion.server.service;

import com.dounion.server.entity.UpgradeRecord;
import com.dounion.server.entity.VersionInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface UpgradeRecordService {

    /**
     * 发布列表查询
     * @return
     */
    List<Map<String, Object>> publishListQuery(Map<String, Object> params);

    List<UpgradeRecord> selectListBySelective(UpgradeRecord record);

    UpgradeRecord load(Integer id);

    int insert(UpgradeRecord record);

    int updateBySelective(UpgradeRecord record);

    PageInfo<UpgradeRecord> page(UpgradeRecord query, int pageSize, int pageNo);

    List<UpgradeRecord> selectEntityListBySelective(UpgradeRecord query);

    void upgradeRecordsGenerate(VersionInfo versionInfo, String subscribeCodes);

}
