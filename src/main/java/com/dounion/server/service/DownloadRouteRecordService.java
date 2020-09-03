package com.dounion.server.service;

import com.dounion.server.entity.DownloadRouteRecord;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DownloadRouteRecordService {
    @Transactional
    void updateDownloadRoute(DownloadRouteRecord record);

    List<DownloadRouteRecord> selectListBySelective(DownloadRouteRecord record);

    int deleteBySelective(DownloadRouteRecord record);
}
