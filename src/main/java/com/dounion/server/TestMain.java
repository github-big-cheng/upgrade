package com.dounion.server;

import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.BeanConfig;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.base.ServiceInfo;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.dao.SubscribeInfoMapper;
import com.dounion.server.entity.SubscribeInfo;
import com.dounion.server.service.TransactionTestService;
import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMain {

    public static void main(String[] args) throws Exception {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(BeanConfig.class);

        SpringApp.init(context);
//        MappingConfigHandler.initialization();

//        transactionTest();
//        pageTest();
//        taskTest();
//        chainTaskTest();
//        taskWakeUpTest();
//        nettyClientTest();

        // 下载内存测试
        downloadMemoryTest();

        while(true) {

        }
    }


    public static void downloadMemoryTest(){

        BaseTask task = new TestTask("download"){
            @Override
            protected void execute() throws Exception {
                NettyClient client = NettyClient.getMasterInstance();
                String fileName = client.fileDownload(Constant.URL_DOWNLOAD + "123.rar");
                System.out.println(fileName);
            }
        };
        BaseTask[] tasks = new BaseTask[10];
        for(int i=0; i<10; i++){
            tasks[i] = task;
        }

        TaskHandler.callTaskChain(null, 5000, tasks);
    }

    public static void nettyClientTest(){

        ServiceInfo serviceInfo = SpringApp.getInstance().getBean(ServiceInfo.class);

        Map<String, Object> params = new HashMap<>();
        params.put("code", serviceInfo.getCode());
        params.put("name", serviceInfo.getName());
        params.put("osType", serviceInfo.getOsType());
        params.put("appType", "40100-140");
        params.put("isStandBy", serviceInfo.getStandBy());
        params.put("publishUrl", serviceInfo.getPublishPath());

        String json = JSONObject.toJSONString(params);
        NettyClient client = NettyClient.getMasterInstance();
        String message = client.doHttpRequest(NettyClient.buildPostMap(Constant.URL_SUBSCRIBE, json));
        System.out.println(message);

        Map<String, Object> params2 = new HashMap<>();
        params2.put("host", "127.0.0.1");
        params2.put("port", 8001);
        params2.put("versionNo", "1.0.0");
        params2.put("appType", "40100-140");
        params2.put("fileName", "appServer.war");
        params2.put("path", Constant.URL_DOWNLOAD + "appServer.war");
        params2.put("downloadPath", "hostAddress" + Constant.URL_DOWNLOAD + "appServer.war");

        NettyClient client2 = NettyClient.getMasterInstance();
        String message2 =
            client2.doHttpRequest(NettyClient.buildPostMap(Constant.URL_ROUTE, JSONObject.toJSONString(params2)));
        System.out.println(message2);
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





    public static void taskWakeUpTest() throws InterruptedException {
        int taskId = TaskHandler.callTask(new TestTask("1", true));

        Thread.sleep(2000l);

        System.out.println(TaskHandler.wakeUp("1"));
        System.out.println(TaskHandler.LOOP_TASK_MAP);
    }





    public static class TestTask extends BaseTask {

        public TestTask(String taskName) {
            this(taskName, false);
        }
        public TestTask(String taskName, boolean isLoop) {
            this.taskName = taskName;
            this.isLoop = isLoop;
        }

        private String taskName;
        private boolean isLoop;

        @Override
        public String getTaskName() {
            return taskName;
        }

        @Override
        public boolean isLoop() {
            return isLoop;
        }

        @Override
        protected void execute() throws Exception {
            System.out.println(this + "我开始执行");
            System.out.println("test:"+Thread.currentThread().getId());
            System.out.println("test:"+Thread.currentThread().getName());
            try {
                Thread.sleep(30000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this + "执行完了");
        }
    }

}
