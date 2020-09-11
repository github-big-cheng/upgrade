package com.dounion.server.service;

import com.dounion.server.entity.VersionInfo;

import java.util.List;

public interface VersionInfoService {


    List<VersionInfo> list(VersionInfo query);

    VersionInfo selectById(Integer id);

    void update(VersionInfo record);

    int updateVersion(VersionInfo record);
}
