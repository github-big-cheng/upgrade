package com.dounion.server.dao;

import com.dounion.server.core.base.BaseDao;
import com.dounion.server.entity.UpgradeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("upgradeRecordMapper")
public class UpgradeRecordMapper extends BaseDao<UpgradeRecord> {

    @Override
    public String getNamespace() {
        return "com.dounion.server.dao.UpgradeRecordMapper";
    }


    public List<Map<String, Object>> publishListQuery(Map<String, Object> params){
        return sql.selectList(this.getNamespace() + ".publishListQuery", params);
    }

    public List<UpgradeRecord> selectEntityListBySelective(UpgradeRecord query) {
        return sql.selectList(this.getNamespace() + ".selectEntityListBySelective", query);
    }

    public void deleteBySelective(UpgradeRecord record) {
        sql.delete(this.getNamespace() + ".deleteBySelective", record);
    }
}
