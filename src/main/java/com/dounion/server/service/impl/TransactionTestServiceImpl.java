package com.dounion.server.service.impl;

import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.service.TransactionTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class TransactionTestServiceImpl implements TransactionTestService {

    @Autowired
    private SubscribeInfoMapper subscribeInfoMapper;

    @Transactional
    @Override
    public void transactionTest() {

        SubscribeInfo subscribeInfo = new SubscribeInfo();
        subscribeInfo.setCode("nnlk118");
        subscribeInfo.setAppType("dosmart-controller");
        subscribeInfo.setIsStandBy("1");
        subscribeInfo.setOsType("windows");
        subscribeInfo.setPublishUrl("http://localhost:1997/notify");
        subscribeInfo.setStatus("1");

        subscribeInfoMapper.insert(subscribeInfo);
        System.out.println(subscribeInfo.getId());

        if(true){
            throw new RuntimeException("test transaction...");
        }

        subscribeInfo.setSubscribeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        subscribeInfo.setStatus("0");
        subscribeInfoMapper.updateByPrimaryKey(subscribeInfo);
    }
}
