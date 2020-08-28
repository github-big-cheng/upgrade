package com.dounion.server.dao;

import com.dounion.server.core.base.BaseDao;
import com.dounion.server.entity.UpgradeRecord;
import org.springframework.stereotype.Repository;

@Repository("upgradeRecordMapper")
public class UpgradeRecordMapper extends BaseDao<UpgradeRecord> {

    @Override
    public String getNamespace() {
        return "com.dounion.server.dao.UpgradeRecordMapper";
    }

}
