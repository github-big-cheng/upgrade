package com.dounion.server.service.impl;

import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.service.SubscribeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private SubscribeInfoMapper subscribeInfoMapper;

    @Override
    public List<SubscribeInfo> list(SubscribeInfo query) {
        return subscribeInfoMapper.selectListBySelective(query);
    }

    @Override
    public PageInfo<SubscribeInfo> page(SubscribeInfo query, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<SubscribeInfo> list = subscribeInfoMapper.selectListBySelective(query);
        return new PageInfo<>(list);
    }

    @Transactional
    @Override
    public void addSubscribe(SubscribeInfo record) {

        String[] appTypes = StringUtils.split(record.getAppType(), ",");
        if(appTypes==null || appTypes.length==0){
            return;
        }

        // 移除本次未注册的服务
        SubscribeInfo delete = new SubscribeInfo();
        delete.setCode(record.getCode());
        delete.setNotInServiceTypes(record.getAppType());
        subscribeInfoMapper.deleteBySelective(delete);

        // 订阅时间
        String time = DateHelper.format(new Date());

        // 遍历处理订阅记录
        for(String appType : appTypes){

            // 查询指定版本是否已订阅指定库点下所有订阅记录
            SubscribeInfo query = new SubscribeInfo();
            query.setCode(record.getCode());
            query.setAppType(appType);
            List<SubscribeInfo> list = subscribeInfoMapper.selectListBySelective(query);

            SubscribeInfo temp;
            if(!CollectionUtils.isEmpty(list)){
                // 已存在记录
                temp = list.get(0);
                record.setId(temp.getId());
                record.setAppType(appType);
                subscribeInfoMapper.updateByPrimaryKeySelective(record);
            } else {
                temp = new SubscribeInfo();
                BeanUtils.copyProperties(record, temp);
                temp.setId(null);
                temp.setAppType(appType);
                temp.setStatus("1");
                temp.setSubscribeTime(time);
                subscribeInfoMapper.insert(temp);
            }

        }
    }


    /**
     * 当前服务订阅信息查询
     * @param query
     * @return
     */
    @Override
    public List<Map<String, String>> currentServiceSubscribeQuery(SubscribeInfo query) {
        return subscribeInfoMapper.currentServiceSubscribeQuery(query);
    }


    @Override
    public void deleteBySelective(SubscribeInfo record){
        subscribeInfoMapper.deleteBySelective(record);
    }
}
