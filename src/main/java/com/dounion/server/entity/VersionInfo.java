package com.dounion.server.entity;

import com.dounion.server.core.base.BaseEntity;
import com.dounion.server.core.helper.StringHelper;

public class VersionInfo extends BaseEntity {

    private Integer id;

    private String appType; // 应用类型

    private String versionNo; // 版本号

    private String isForceUpdate; // 是否强制更新

    private String fileName; // 文件名称

    private String filePath; // 文件保存路径

    private String status; // 版本状态 1-正常 2-已注销

    private String addSource; // 版本来源 1-本地发布 2-远程发布

    private String publishDate; // 发布日期

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType == null ? null : appType.trim();
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo == null ? null : versionNo.trim();
    }

    public String getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(String isForceUpdate) {
        this.isForceUpdate = isForceUpdate == null ? null : isForceUpdate.trim();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        if(this.fileName != null){
            return this.fileName;
        }
        return StringHelper.getFileName(this.filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
    }

    public String getStatus() {
        return status;
    }

    public String getAddSource() {
        return addSource;
    }

    public void setAddSource(String addSource) {
        this.addSource = addSource;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}