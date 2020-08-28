package com.dounion.server.entity;

import com.dounion.server.core.base.BaseEntity;

public class SubscribeInfo extends BaseEntity {

    private Integer id;

    private String code;

    private String appType;

    private String osType;

    private String isStandBy;

    private String publishUrl;

    private String status;

    private String subscribeTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType == null ? null : appType.trim();
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType == null ? null : osType.trim();
    }

    public String getIsStandBy() {
        return isStandBy;
    }

    public void setIsStandBy(String isStandBy) {
        this.isStandBy = isStandBy == null ? null : isStandBy.trim();
    }

    public String getPublishUrl() {
        return publishUrl;
    }

    public void setPublishUrl(String publishUrl) {
        this.publishUrl = publishUrl == null ? null : publishUrl.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime == null ? null : subscribeTime.trim();
    }
}