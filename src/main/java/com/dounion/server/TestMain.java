package com.dounion.server;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.request.MappingConfigHandler;
import com.dounion.server.core.task.TaskHandler;
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
//        MappingConfigHandler.initialization();

//        transactionTest();
//        pageTest();

//        taskTest();

        chainTaskTest();

        while(true) {

        }
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


    /**
     * 后台任务测试
     */
    public static void taskTest(){
        int id1 = TaskHandler.callTask("testTask");
        int id2 = TaskHandler.callTask("testTask");

        System.out.println(id1);
        System.out.println(id2);

        for(BaseTask task : TaskHandler.getTaskList()){
            System.out.println(task);
        }

        TaskHandler.interrupted(id1);
    }
    
    
    public static void chainTaskTest(){
        TaskHandler.callTaskChain(null, 0, new TestTask("1"), new TestTask("2"));
        System.out.println(TaskHandler.getTaskList());
    }


    public static class TestTask extends BaseTask {


        public TestTask(String taskName) {
            this.taskName = taskName;
        }

        private String taskName;

        @Override
        public String getTaskName() {
            return taskName;
        }

        @Override
        protected void execute() throws Exception {
            System.out.println(this + "我开始执行");

            Thread.sleep(3000l);

            System.out.println(this + "执行完了");
        }
    }

}
