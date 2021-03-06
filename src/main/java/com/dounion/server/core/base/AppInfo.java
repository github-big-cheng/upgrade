package com.dounion.server.core.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.dounion.server.core.helper.StringHelper;
import com.dounion.server.eum.AppTypeEnum;
import com.dounion.server.eum.DeployTypeEnum;

public class AppInfo {

    // 所属服务信息
    @JSONField(serialize = false, deserialize = false)
    private ServiceInfo serviceInfo;

    private String appType; // 服务类型
    private String versionNo; // 版本号
    private String workPath; // 工作路径
    private String username; // 用户名 mysql 等
    private String password; // 密码


    // ============================= extended method  ==============================

    @Override
    public String toString() {
        return "appInfo : " + StringHelper.jsonFormatString(this);
    }


    // ============================= getter and setter  ==============================

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppType() {
        return appType;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getWorkPath() {
        return workPath;
    }

    public void setWorkPath(String workPath) {
        this.workPath = workPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JSONField(deserialize = false)
    public AppTypeEnum getAppTypeEnum(){
        return AppTypeEnum.getMap().get(this.appType);
    }

    @JSONField(deserialize = false)
    public String getAppName(){
        return this.getAppTypeEnum()==null ? null : this.getAppTypeEnum().getAppName();
    }

    @JSONField(deserialize = false)
    public DeployTypeEnum getDeployTypeEnum(){
        AppTypeEnum appTypeEnum = this.getAppTypeEnum();
        return appTypeEnum==null ? null : appTypeEnum.getDeployType();
    }
}
