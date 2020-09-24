package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.BusinessException;
import com.dounion.server.core.helper.FileHelper;
import com.dounion.server.core.helper.StringHelper;
import com.dounion.server.core.netty.client.NettyClient;
import com.dounion.server.core.task.TaskHandler;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.entity.VersionInfo;
import com.dounion.server.service.VersionInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.File;

/**
 * 资源下载后台任务
 */
@Task(Constant.TASK_DOWNLOAD)
public class DownloadTask extends BaseTask {


    @Autowired
    private VersionInfoService versionInfoService;

    @Override
    public String getTaskName() {
        return "资源下载后台任务";
    }

    @Override
    protected void execute() throws Exception {

        if(CollectionUtils.isEmpty(super.params) ||
                            super.params.get("versionId")==null){
            throw new BusinessException("versionId is needed");
        }

        final Integer id = (Integer) super.params.get("versionId");
        VersionInfo versionInfo = versionInfoService.selectById(id);
        if(versionInfo == null){
            throw new BusinessException("【versionId:" + id + "】 not found, please check it");
        }

        // 根据版本记录表文件路径判断是否已下载
        if(StringUtils.isNotBlank(versionInfo.getFilePath())){
            logger.info("【{}】 file might having been download 【{}】, task exit", this, versionInfo.getFilePath());
            return;
        }
        if(StringUtils.isBlank(versionInfo.getFileName())){
            throw new BusinessException("【versionId:" + id + "】 not found, please check it");
        }

        String downloadUrl = Constant.URL_DOWNLOAD + versionInfo.getFileName();
        String filePath = NettyClient.getMasterInstance().fileDownload(downloadUrl);
        logger.debug("filePath is 【{}】", filePath);

        // 检查文件是否下载成功
        if(StringUtils.isBlank(filePath)){
            throw new BusinessException(StringHelper.parse1("【{}】下载失败", downloadUrl));
        }
        File file = new File(filePath);
        // 检查文件是否下载成功
        if(!file.exists()){
            throw new BusinessException(StringHelper.parse1("【{}】下载失败", downloadUrl));
        }
        // 文件大小检查
        if(file.length() != versionInfo.getFileSize()){
            logger.error("【{}】 file size check failed, expect 【{}】, but 【{}】",
                    this, versionInfo.getFileSize(), file.length());
            throw new BusinessException(StringHelper.parse1("【{}】文件大小校验失败", downloadUrl));
        }
        // 检查文件MD5值
        String fileMd5 = FileHelper.getFileMD5(file);
        if(!StringUtils.equals(fileMd5, versionInfo.getFileMd5())){
            logger.error("【{}】 MD5 check failed, expect 【{}】, but 【{}】",
                    this, versionInfo.getFileMd5(), fileMd5);
            throw new BusinessException(StringHelper.parse1("【{}】文件MD5值校验失败", downloadUrl));
        }

        // 更新文件路径
        versionInfo.setFilePath(filePath);
        versionInfo.setFileSize(file.length());
        versionInfo.setFileMd5(fileMd5);
        versionInfoService.update(versionInfo);

        super.setProgressComplete(); // progress 100%

        // 调用路由注册服务
        TaskHandler.wakeUp(Constant.TASK_ROUTE);
    }
}
