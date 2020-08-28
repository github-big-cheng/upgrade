package com.dounion.server.core.base;

import com.dounion.server.eum.ServiceTypeEnum;

public class AppInfo {

    // 所属服务信息
    private ServiceInfo serviceInfo;

    private String serviceType; // 服务类型
    private String versionNo; // 版本号
    private String workPath; // 工作路径


    // ============================= extended method  ==============================

    /**
     * 获取服务类型
     * @return
     */
    public ServiceTypeEnum getServiceType(){
        return ServiceTypeEnum.getMap().get(this.serviceType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("appInfo : {");
        sb.append("serviceType:").append(this.getServiceType()).append(", \t")
          .append("versionNo:").append(this.versionNo).append(", \t")
          .append("workPath:").append(this.workPath).append(", \t")
        ;
        sb.append("}");
        return sb.toString();
    }


    // ============================= getter and setter  ==============================

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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
}
