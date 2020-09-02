package com.dounion.server.entity;

import com.dounion.server.core.base.BaseEntity;

public class SubscribeInfo extends BaseEntity {

    private Integer id;

    private String code; // 库点代码

    private String name; // 库点名称

    private String appType; // 应用类型

    private String osType; // 操作系统类型

    private String isStandBy; // 是否提供分发下载服务 1-是 0-否

    private String status; // 订阅状态 0-已注销 1-已订阅

    private String publishUrl; // 回调地址

    private String subscribeTime; // 订阅事件

    // 查询条件
    private String codes;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }
}