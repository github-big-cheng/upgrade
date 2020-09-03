package com.dounion.server.service.impl;

import com.dounion.server.dao.DownloadRouteRecordMapper;
import com.dounion.server.dao.VersionInfoMapper;
import com.dounion.server.entity.DownloadRouteRecord;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.service.DownloadRouteRecordService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DownloadRouteRecordServiceImpl implements DownloadRouteRecordService {

    private final static Logger logger = LoggerFactory.getLogger(DownloadRouteRecordServiceImpl.class);

    @Autowired
    private VersionInfoMapper versionInfoMapper;
    @Autowired
    private DownloadRouteRecordMapper downloadRouteRecordMapper;


    @Transactional
    @Override
    public void updateDownloadRoute(DownloadRouteRecord record){

        // 检查必要参数
        Assert.notNull(record.getHost(), "host must not be null");
        Assert.notNull(record.getAppType(), "host must not be null");
        Assert.notNull(record.getVersionNo(), "versionNo must not be null");
        Assert.notNull(record.getDownloadPath(), "downloadPath must not be null");

        // 按主机+应用类型删除指定记录
        DownloadRouteRecord delete = new DownloadRouteRecord();
        delete.setHost(record.getHost());
        delete.setAppType(record.getAppType());
        downloadRouteRecordMapper.deleteBySelective(delete);



        // 检查是否当前版本
        VersionInfo versionQuery = new VersionInfo();
        versionQuery.setAppType(record.getAppType());
        List<VersionInfo> versionInfos = versionInfoMapper.selectListBySelective(versionQuery);
        if(CollectionUtils.isEmpty(versionInfos) ||
                !StringUtils.equals(versionInfos.get(0).getVersionNo(), record.getVersionNo())){
            logger.info("versionNo is [{}], current version is [{}] ,version check not access, exit",
                    record.getVersionNo(), versionInfos.get(0).getVersionNo());
            return;
        }

        // 插入新记录
        downloadRouteRecordMapper.insert(record);
    }


    @Override
    public List<DownloadRouteRecord> selectListBySelective(DownloadRouteRecord record) {
        return downloadRouteRecordMapper.selectListBySelective(record);
    }

    @Override
    public int deleteBySelective(DownloadRouteRecord record){
        return downloadRouteRecordMapper.deleteBySelective(record);
    }


}
