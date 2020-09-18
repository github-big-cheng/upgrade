package com.dounion.server.deploy.app.impl;

import com.dounion.server.core.deploy.annotation.Deploy;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.helper.DateHelper;
import com.dounion.server.eum.DeployTypeEnum;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


@Deploy(deployType = DeployTypeEnum.PROPERTIES)
public class PropertiesScript extends RestartScript{

    final static String CONVERT_CHAR = "Save:|"; // 保存/覆盖操作
    final static String APPEND_CHAR = "Append:|"; // 追加操作

    final Charset charset = Charset.defaultCharset();

    @Override
    public void deploy() {

        if(params==null || params.getVersionInfo()==null || params.getAppInfo()==null){
            throw new BusinessException("PropertiesScript: please check params");
        }

        // 解析上传的版本文件
        File versionFile = new File(params.getVersionInfo().getFilePath());
        if(!versionFile.exists()){
            logger.error("PropertiesScript: version file 【{}】 isn't exists", params.getVersionInfo().getFilePath());
            return;
        }

        // 原文件
        List<String> content = new ArrayList<>();
        LinkedHashMap<String, Integer> originLine = new LinkedHashMap<>();

        String originFileName = params.getAppInfo().getWorkPath() +
                        File.separator + params.getVersionInfo().getFileName();
        File originFile = new File(originFileName);
        if(originFile.exists()){
            // 检查原文件存在，解析
            this.fileParser(originFile, content, originLine);
        }

        // 文件组装
        propertiesMixed(versionFile, content, originLine);

        // 文件备份
        String date = DateHelper.format(new Date(), "yyyyMMddHHmmss");
        File backFile = new File(originFileName + "." + date);
        originFile.renameTo(backFile);

        // 写入新文件
        File f = new File(originFileName);
        try {
            Files.write(
                f.toPath(), // 文件路径
                content, // 文件内容
                charset // 编码
            );
        } catch (IOException e) {
            logger.error("PropertiesScript: file write error:{}", e);

            // 还原操作
            if(f.exists()){
                f.delete();
            }
            backFile.renameTo(f);

            throw new SystemException("PropertiesScript: file write failed");
        }

        // 调用父类重启脚本
        // TODO appInfo对应不上，待完善
//        super.deploy();
    }

    /**
     * 处理配置文件修改
     * @param versionFile 版本文件
     * @param content 文件内容
     * @param originLine 原文件map集合
     */
    private void propertiesMixed(File versionFile,
                         List<String> content, LinkedHashMap<String, Integer> originLine) {

        if(content == null){
            content = new ArrayList<>();
        }

        // 遍历处理版本文件
        try (
                BufferedReader reader =
                     Files.newBufferedReader(versionFile.toPath(), charset)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                line = StringUtils.trim(line);

                // 追加操作
                boolean append = false;
                String realLine = line;
                if (StringUtils.startsWith(line, APPEND_CHAR)) {
                    realLine = StringUtils.substring(line, APPEND_CHAR.length());
                    append = true;
                }
                if (StringUtils.startsWith(line, CONVERT_CHAR)) {
                    realLine = StringUtils.substring(line, CONVERT_CHAR.length());
                }

                // 注释 直接添加
                // 暂不处理注释
//                if (StringUtils.startsWith(realLine, "#")) {
//                    content.add(realLine);
//                    continue;
//                }

                String[] arr = StringUtils.split(realLine, "=", 2);
                if (arr.length != 2) {
                    logger.warn("properties line mixed failed..[{}]", realLine);
                    continue;
                }

                Integer index = originLine.get(arr[0].trim());
                // 原文件中存在指定键值
                if (index != null) {
                    // 追加
                    if (append) {
                        content.set(index, content.get(index).trim() + arr[1].trim());
                    } else {
                        content.set(index, realLine);
                    }
                    continue;
                }

                // 默认追加
                content.add(realLine);
            }
        } catch (MalformedInputException e) {

        } catch (IOException e) {
            logger.error("propertiesMixed: 文件【{}】处理异常{}, e:{}", versionFile.getName(), e);
            throw new SystemException("file parser error:" + versionFile.getAbsolutePath());
        }
    }


    /**
     * 文件解析
     * @param file
     * @param content
     * @param map
     * @return
     */
    private void fileParser(File file, List<String> content, LinkedHashMap<String, Integer> map){

        try (
                BufferedReader reader =
                        Files.newBufferedReader(file.toPath(), charset)
        ){
            int lineNum = 0;
            String line;
            while((line=reader.readLine()) != null) {

                line = StringUtils.trim(line);

                content.add(line);

                // 非注释及空行
                if(StringUtils.isNotBlank(line) &&
                        !StringUtils.startsWith(line, "#")){
                    String[] arr = StringUtils.split(line, "=", 2);
                    if(arr.length == 2){
                        map.put(arr[0].trim(), lineNum);
                    }
                }

                lineNum ++;
            }
        } catch (IOException e) {
            logger.error("fileParser: 文件【{}】解析异常{}, e:{}", file.getName(), e);
            throw new SystemException("file parser error:" + file.getAbsolutePath());
        }

    }
}
