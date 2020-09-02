package com.dounion.server.service.impl;

import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.dao.UpgradeRecordMapper;
import com.dounion.server.dao.VersionInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.entity.UpgradeRecord;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.service.UpgradeRecordService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
public class UpgradeRecordServiceImpl implements UpgradeRecordService {

    private final static Logger logger = LoggerFactory.getLogger(UpgradeRecordServiceImpl.class);

    @Autowired
    private VersionInfoMapper versionInfoMapper;
    @Autowired
    private SubscribeInfoMapper subscribeInfoMapper;
    @Autowired
    private UpgradeRecordMapper upgradeRecordMapper;

    @Override
    public List<Map<String, Object>> publishListQuery(Map<String, Object> params) {
        return upgradeRecordMapper.publishListQuery(params);
    }


    @Override
    public List<UpgradeRecord> selectListBySelective(UpgradeRecord record) {
        return upgradeRecordMapper.selectListBySelective(record);
    }

    @Override
    public int insert(UpgradeRecord record) {
        upgradeRecordMapper.insert(record);
        return record.getId();
    }


    @Override
    public int updateBySelective(UpgradeRecord record){
        return upgradeRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<UpgradeRecord> selectEntityListBySelective(UpgradeRecord query) {
        return upgradeRecordMapper.selectEntityListBySelective(query);
    }


    /**
     * 生成手工更新记录
     * @param versionInfo
     * @param subscribeCodes
     */
    @Transactional
    @Override
    public void upgradeRecordsGenerate(VersionInfo versionInfo, String subscribeCodes) {
        if(StringUtils.isBlank(subscribeCodes)){
            logger.warn("UpgradeRecordService.upgradeRecordsGenerate():subscribeCodes is null");
            return;
        }

        versionInfo = versionInfoMapper.selectByPrimaryKey(versionInfo.getId());
        if(versionInfo == null){
            logger.warn("UpgradeRecordService.upgradeRecordsGenerate():versionInfo is null");
            return;
        }

        SubscribeInfo query = new SubscribeInfo();
        query.setCodes(subscribeCodes);
        List<SubscribeInfo> subscribeInfos = subscribeInfoMapper.selectListBySelective(query);
        if(CollectionUtils.isEmpty(subscribeInfos)){
            logger.warn("UpgradeRecordService.upgradeRecordsGenerate():subscribeInfos is empty");
            return;
        }

        UpgradeRecord record;
        for(SubscribeInfo subscribeInfo : subscribeInfos){
            record = new UpgradeRecord();
            record.setVersionId(versionInfo.getId());
            record.setVersionNo(versionInfo.getVersionNo());
            record.setAppType(versionInfo.getAppType());
            record.setSubscribeId(subscribeInfo.getId());
            record.setCode(subscribeInfo.getCode());
            record.setName(subscribeInfo.getName());
            record.setPublishUrl(subscribeInfo.getPublishUrl());
            record.setNotifyCount(0);
            upgradeRecordMapper.insert(record);
        }
    }
}
