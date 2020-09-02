package com.dounion.server.core.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.dounion.server.core.helper.FileHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceInfo {

    private final static Logger logger = LoggerFactory.getLogger(ServiceInfo.class);

    // 库点代码
    private String code;

    // 库点名称
    private String name;

    // 本地IP
    private String localIp;

    // 端口
    private Integer port;

    // 是否主服务
    private String master;

    private String ignoreMode;

    // 上级服务地址
    private String masterIp;

    // 上级服务端口
    private Integer masterPort;

    // 操作系统类型
    private String osType;

    // 本地注册的服务
    private String localServices;

    // 是否提供分发下载服务
    private String standBy;

    // 通知地址
    private String publishPath;

    // 本地注册服务列表
    @JSONField(serialize = false)
    private List<AppInfo> localServiceList;


    // ============================= extended method  ==============================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceInfo :{");

        sb.append("code:").append(this.code).append(", \t")
            .append("name:").append(this.name).append(", \t")
            .append("localIp:").append(this.localIp).append(", \t")
            .append("port:").append(this.port).append(", \t")
            .append("master:").append(this.getMaster()).append(", \t")
            .append("masterIp:").append(this.masterIp).append(", \t")
            .append("masterPort:").append(this.masterPort).append(", \t")
            .append("osType:").append(this.getOsType()).append(", \t")
            .append("standBy:").append(this.getStandBy()).append(", \t")
            .append("publishPath:").append(this.getPublishPath()).append(", \t")
        ;

        List<AppInfo> appList = this.getLocalServiceList();
        if(!CollectionUtils.isEmpty(appList)){
            sb.append("\r\n localServices :[");
            for(AppInfo app : appList){
                sb.append(app.toString()).append(",\t");
            }
            sb.append("}");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 是否主服务
     * @return
     */
    public boolean getMasterBlur() {
        return StringUtils.equals(master, "1");
    }

    /**
     * 是否备用下载
     * @return
     */
    public boolean getStandByBlur() {
        return StringUtils.equals(standBy, "1");
    }


    /**
     * 获取本地服务列表
     * @return
     */
    public List<AppInfo> getLocalServiceList() {
        if(this.localServiceList == null){
            this.localServiceList = new ArrayList<>();
            JSONArray array = JSON.parseArray(this.localServices);
            for(int i=0; i<array.size(); i++){
                this.localServiceList.add(array.getObject(i, AppInfo.class));
            }
        }
        return localServiceList;
    }

    /**
     * 重新加载本地服务列表
     */
    public void reloadLocalService(){
        this.localServiceList = null;
    }

    public void toFile(){
        try {
            FileHelper.writeFile(Constant.PATH_CONF + Constant.FILE_JSON_CONFIG_NAME, JSONObject.toJSONString(this));
        } catch (IOException e) {
            logger.error("ServiceInfo write to file error: {}", e);
        }
    }

    // ============================= getter and setter  ==============================

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getIgnoreMode() {
        return ignoreMode;
    }

    public void setIgnoreMode(String ignoreMode) {
        this.ignoreMode = ignoreMode;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public Integer getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(Integer masterPort) {
        this.masterPort = masterPort;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsType(){
        return this.osType;
    }

    public String getStandBy() {
        return standBy;
    }

    public void setStandBy(String standBy) {
        this.standBy = standBy;
    }

    public String getPublishPath() {
        Assert.notNull(this.localIp, "localIp must be config");
        Assert.notNull(this.port, "port must be config");
        if(StringUtils.isBlank(this.publishPath)){
            this.publishPath = "http://" + this.localIp + ":" +this.port + Constant.URL_PUBLISH;
        }
        return publishPath;
    }

    public void setPublishPath(String publishPath) {
        this.publishPath = publishPath;
    }

    public String getLocalServices() {
        this.getLocalServiceList();
        return localServices;
    }

    public void setLocalServices(String localServices) {
        this.localServices = localServices;
    }
}
