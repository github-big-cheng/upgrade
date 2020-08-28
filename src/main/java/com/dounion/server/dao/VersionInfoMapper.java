package com.dounion.server.dao;

import com.dounion.server.core.base.BaseDao;
import com.dounion.server.entity.VersionInfo;
import org.springframework.stereotype.Repository;

@Repository("versionInfoMapper")
public class VersionInfoMapper extends BaseDao<VersionInfo> {

    @Override
    public String getNamespace() {
        return "com.dounion.server.dao.VersionInfoMapper";
    }

    public void deleteBySelective(VersionInfo record) {
        sql.delete(this.getNamespace() + ".deleteBySelective", record);
    }
}
