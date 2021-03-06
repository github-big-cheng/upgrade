package com.dounion.server.core.deploy;

import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.core.helper.SpringApp;
import com.dounion.server.eum.DeployTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 发布处理器
 */
public class DeployHandler {

    // 私有化构造方法
    private DeployHandler(){
    }


    private static Logger logger = LoggerFactory.getLogger(DeployHandler.class);

    private final static ExecutorService POOL = Executors.newFixedThreadPool(3);

    private final static Map<DeployTypeEnum, Object> DEPLOY_BEAN_MAP = new HashMap<>();


    /**
     * 初始化
     */
    public static void initialization(){
        Map<String, Object> beanMap =
                SpringApp.getInstance().getObjectByAnnotationType(Deploy.class);
        if(CollectionUtils.isEmpty(beanMap)){
            return;
        }

        Deploy deploy;
        Object o;
        for(String name : beanMap.keySet()){
            o = beanMap.get(name);
            deploy = o.getClass().getAnnotation(Deploy.class);
            DEPLOY_BEAN_MAP.put(deploy.deployType(), o);
        }

    }

    /**
     * 获取部署适配器
     * @param deployType
     * @param <T>
     * @return
     */
    public static <T> T getDeploy(DeployTypeEnum deployType){
        return (T) DEPLOY_BEAN_MAP.get(deployType);
    }


    /**
     * 执行脚本命令
     * @param workDir
     * @param cmd
     */
    public static void execute(String workDir, String... cmd) {

        logger.debug(Arrays.toString(cmd));

        Process p = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            builder.directory(new File(workDir));
            p = builder.start();

            final Process finalP = p;
            Future<Integer> future = POOL.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    finalP.waitFor();
                    return finalP.exitValue();
                }
            });

            final CountDownLatch latch = new CountDownLatch(2);

            new CmdOuterCleanThread(latch, p.getInputStream(), "INFO").start();
            // redirectErrorStream ERROR 不应该被打印
            new CmdOuterCleanThread(latch, p.getErrorStream(), "ERROR").start();

            latch.await();

            logger.info("Exit value: {}", future.get(10, TimeUnit.SECONDS));

        } catch (Exception e) {
            logger.error("DeployHandler.execute error:{}", e);
        } finally {
            if(p != null){
                logger.debug("progress destroy...");
                p.destroy();
            }
        }
    }


    static class CmdOuterCleanThread extends Thread {
        CountDownLatch latch;
        InputStream is;
        String type;

        CmdOuterCleanThread(CountDownLatch latch, InputStream is, String type) {
            this.latch = latch;
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    logger.info("{}:{}", this.type, line);
                }
            } catch (IOException e) {
                logger.error("read {} error:{}", this.type, e);
            } finally {
                if(latch != null){
                    latch.countDown();
                }
            }
        }

    }
}
