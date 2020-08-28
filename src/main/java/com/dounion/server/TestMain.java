package com.dounion.server;

import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.request.HandlerMappingConfig;
import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.service.TransactionTestService;
import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class TestMain {

    public static void main(String[] args) throws Exception {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(BeanConfig.class);

        SpringApp.init(context);
        HandlerMappingConfig.initialization();

//        transactionTest();
//        pageTest();
    }



    static void transactionTest(){
        // 事务测试
        TransactionTestService service = SpringApp.getInstance().getBean(TransactionTestService.class);
        service.transactionTest();
    }


    static void pageTest(){
        // 分页查询测试
        SubscribeInfoMapper mapper = SpringApp.getInstance().getBean(SubscribeInfoMapper.class);
        SubscribeInfo query = new SubscribeInfo();
        query.setAppType("dosmart-controller");
        PageHelper.startPage(1, 5);
        List<SubscribeInfo> list = mapper.selectListBySelective(query);
        System.out.println(list.size());
    }

}