package com.dounion.server.service;

import com.dounion.server.entity.VersionInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VersionInfoService {


    List<VersionInfo> list(VersionInfo query);

    VersionInfo selectById(Integer id);

    void update(VersionInfo record);

    @Transactional
    void updateVersion(VersionInfo record);
}
