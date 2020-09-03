package com.dounion.server.dao;

import com.dounion.server.core.base.BaseDao;
import com.dounion.server.entity.DownloadRouteRecord;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("downloadRouteRecordMapper")
public class DownloadRouteRecordMapper extends BaseDao<DownloadRouteRecord> {

    @Override
    public String getNamespace() {
        return "com.dounion.server.dao.DownloadRouteRecordMapper";
    }


    public int deleteBySelective(DownloadRouteRecord record) {
        Assert.notNull(record.getAppType(), "appType needed at lease");
        return super.sql.delete(this.getNamespace() + ".deleteBySelective", record);
    }

}
