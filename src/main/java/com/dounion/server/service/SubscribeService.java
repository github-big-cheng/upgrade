package com.dounion.server.service;

import com.dounion.server.entity.SubscribeInfo;

import java.util.List;
import java.util.Map;

public interface SubscribeService {

    List<SubscribeInfo> list(SubscribeInfo query);

    void addSubscribe(SubscribeInfo record);

    List<Map<String, String>> currentServiceSubscribeQuery(SubscribeInfo query);

    void deleteBySelective(SubscribeInfo record);
}
