package com.dounion.server.core.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 发布处理器
 */
public class DeployHandler {


    private static Logger logger = LoggerFactory.getLogger(DeployHandler.class);


    /**
     * 执行脚本命令
     * @param cmd
     */
    public static void execute(String ...cmd){
        StringBuffer sb = new StringBuffer();
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.defaultCharset()));

            String line;
            while((line = bri.readLine()) != null) {
                sb.append(line + "\n");
            }

            bri.close();
            Integer exitValue = p.waitFor();

            logger.info("Exit value: {}, \r\nOutput:{}", exitValue, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
