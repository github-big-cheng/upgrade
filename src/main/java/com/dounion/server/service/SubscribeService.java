package com.dounion.server.service;

import com.dounion.server.entity.SubscribeInfo;

import java.util.List;

public interface SubscribeService {

    List<SubscribeInfo> list(SubscribeInfo query);

    void addSubscribe(SubscribeInfo record);

    List<String> currentServiceSubscribeQuery(SubscribeInfo query);

    void deleteBySelective(SubscribeInfo record);
}
