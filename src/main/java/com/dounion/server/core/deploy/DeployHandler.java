package com.dounion.server.core.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 发布处理器
 */
public class DeployHandler {


    private static Logger logger = LoggerFactory.getLogger(DeployHandler.class);

    private final static ExecutorService POOL = Executors.newFixedThreadPool(3);

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

            new CmdOuterCleanThread(p.getInputStream(), "INFO").start();
            // redirectErrorStream ERROR 不应该被打印
            new CmdOuterCleanThread(p.getErrorStream(), "ERROR").start();

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
        InputStream is;
        String type;

        CmdOuterCleanThread(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    logger.debug("{}:{}", this.type, line);
                }
            } catch (IOException e) {
                logger.error("read {} error:{}", this.type, e);
            }
        }

    }
}
