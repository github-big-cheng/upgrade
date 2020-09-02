package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.task.annotation.Task;
import org.springframework.util.CollectionUtils;

/**
 * 资源下载后台任务
 */
@Task(Constant.TASK_DOWNLOAD)
public class DownloadTask extends BaseTask {


    @Override
    public String getTaskName() {
        return "资源下载后台任务";
    }

    @Override
    protected void execute() throws Exception {

        if(CollectionUtils.isEmpty(super.params) ||
                            super.params.get("versionId")==null){
            throw new SystemException("versionId is needed");
        }



    }
}
