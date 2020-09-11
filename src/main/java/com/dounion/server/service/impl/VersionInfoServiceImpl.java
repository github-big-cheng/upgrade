package com.dounion.server.service.impl;

import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.dao.VersionInfoMapper;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.service.VersionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class VersionInfoServiceImpl implements VersionInfoService {

    @Autowired
    private VersionInfoMapper versionInfoMapper;

    @Override
    public List<VersionInfo> list(VersionInfo query) {
        return versionInfoMapper.selectListBySelective(query);
    }


    @Override
    public VersionInfo selectById(Integer id){
        return versionInfoMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(VersionInfo record) {
        versionInfoMapper.updateByPrimaryKeySelective(record);
    }

    @Transactional
    @Override
    public int updateVersion(VersionInfo record) {

        // 删除已有版本信息
        VersionInfo delete = new VersionInfo();
        delete.setAppType(record.getAppType());
        versionInfoMapper.deleteBySelective(delete);

        // 保存版本记录
        record.setStatus("1");
        record.setPublishDate(DateHelper.format(new Date()));
        versionInfoMapper.insert(record);
        return record.getId();
    }
}
