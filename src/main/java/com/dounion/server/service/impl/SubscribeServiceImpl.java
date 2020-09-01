package com.dounion.server.service.impl;

import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.service.SubscribeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private SubscribeInfoMapper subscribeInfoMapper;

    @Override
    public List<SubscribeInfo> list(SubscribeInfo query) {
        return subscribeInfoMapper.selectListBySelective(query);
    }

    @Transactional
    @Override
    public void addSubscribe(SubscribeInfo record) {

        String[] services = StringUtils.split(record.getAppType(), ",");
        if(services==null || services.length==0){
            return;
        }

        // 删除指定库点下所有订阅记录
        SubscribeInfo delete = new SubscribeInfo();
        delete.setCode(record.getCode());
        subscribeInfoMapper.deleteBySelective(delete);

        String time = DateHelper.format(new Date());
        SubscribeInfo temp;
        for(String service : services){
            temp = new SubscribeInfo();
            BeanUtils.copyProperties(record, temp);
            temp.setAppType(service);
            temp.setStatus("1");
            temp.setSubscribeTime(time);
            subscribeInfoMapper.insert(temp);
        }
    }


    /**
     * 当前服务订阅信息查询
     * @param query
     * @return
     */
    @Override
    public List<String> currentServiceSubscribeQuery(SubscribeInfo query) {
        return subscribeInfoMapper.currentServiceSubscribeQuery(query);
    }


    @Override
    public void deleteBySelective(SubscribeInfo record){
        subscribeInfoMapper.deleteBySelective(record);
    }
}
