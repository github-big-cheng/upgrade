package com.dounion.server.task;

import com.dounion.server.core.base.BaseTask;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.task.annotation.Task;
import com.dounion.server.dao.UpgradeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 更新通知后台任务
 */
@Task(Constant.TASK_PUBLISH)
public class PublishTask extends BaseTask {


    @Autowired
    private UpgradeRecordMapper upgradeRecordMapper;

    @Override
    public String getTaskName() {
        return "部署后台任务";
    }

    @Override
    protected void execute() {

//        List<>upgradeRecordMapper.selectListBySelective();

    }
}
