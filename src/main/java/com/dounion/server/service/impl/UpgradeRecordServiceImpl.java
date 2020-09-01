package com.dounion.server.service.impl;

import com.dounion.server.dao.UpgradeRecordMapper;
import com.dounion.server.service.UpgradeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UpgradeRecordServiceImpl implements UpgradeRecordService {

    @Autowired
    private UpgradeRecordMapper upgradeRecordMapper;

    @Override
    public List<Map<String, Object>> publishListQuery() {
        return upgradeRecordMapper.publishListQuery();
    }
}
