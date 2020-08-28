package com.dounion.server.service;

import com.dounion.server.entity.VersionInfo;

import java.util.List;

public interface VersionInfoService {


    List<VersionInfo> list(VersionInfo query);

    void update(VersionInfo record);
}
