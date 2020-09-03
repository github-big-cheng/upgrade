package com.dounion.server.service.impl;

import com.dounion.server.dao.DownloadRouteRecordMapper;
import com.dounion.server.entity.DownloadRouteRecord;
import com.dounion.server.service.DownloadRouteRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class DownloadRouteRecordServiceImpl implements DownloadRouteRecordService {

    @Autowired
    private DownloadRouteRecordMapper downloadRouteRecordMapper;


    @Transactional
    @Override
    public void updateDownloadRoute(DownloadRouteRecord record){

        // 检查必要参数
        Assert.notNull(record.getHost(), "host must not be null");
        Assert.notNull(record.getAppType(), "host must not be null");
        Assert.notNull(record.getDownloadPath(), "downloadPath must not be null");

        // 按主机+应用类型删除指定记录
        DownloadRouteRecord delete = new DownloadRouteRecord();
        delete.setHost(record.getHost());
        delete.setAppType(record.getAppType());
        downloadRouteRecordMapper.deleteBySelective(delete);

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
