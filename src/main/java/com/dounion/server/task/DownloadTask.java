package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.BusinessException;
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
            logger.info("【】 file might having been download, task exit", this);
            return;
        }

        String downloadUrl = Constant.URL_DOWNLOAD + versionInfo.getFileName();
        String filePath = NettyClient.getMasterInstance().fileDownload(downloadUrl);

        File file = new File(filePath);
        if(!file.exists()){
            throw new BusinessException(StringHelper.parse1("【{}】下载失败", downloadUrl));
        }

        // 更新文件路径
        versionInfo.setFilePath(filePath);
        versionInfo.setFileSize(file.length());
        versionInfoService.update(versionInfo);

        // 调用路由注册服务
        TaskHandler.callTask(new RouteTask(){
            @Override
            public String getTaskName() {
                return "部署后台任务-" + id ;
            }

            @Override
            public boolean isLoop() {
                return false;
            }
        });
    }
}
