package com.dounion.server.core.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.helper.FileHelper;
import com.dounion.server.eum.OsTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceInfo {

    private final static Logger logger = LoggerFactory.getLogger(ServiceInfo.class);

    // 库点代码
    private String code;

    // 是否主服务
    private String isMaster;

    // 本地IP
    private String localIp;

    // 端口
    private int port;

    // 操作系统类型
    private String osType;

    // 本地注册的服务
    private String localServices;

    // 是否提供分发下载服务
    private String isStandBy;

    // 通知地址
    private String publishPath;

    // 本地注册服务列表
    private List<AppInfo> localServiceList;


    // ============================= extended method  ==============================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServerInfo :{");

        sb.append("code:").append(this.code).append(", \t")
            .append("localIp:").append(this.localIp).append(", \t")
            .append("port:").append(this.port).append(", \t")
            .append("osType:").append(this.getOsType()).append(", \t")
            .append("isStandBy:").append(this.isStandBy()).append(", \t")
            .append("publishPath:").append(this.getPublishPath()).append(", \t")
        ;

        List<AppInfo> appList = this.getLocalServiceList();
        if(!CollectionUtils.isEmpty(appList)){
            sb.append("\r\n localServices :[");
            for(AppInfo app : appList){
                sb.append(app.toString());
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
    public boolean isMaster() {
        return StringUtils.equals(isMaster, "1");
    }

    /**
     * 操作系统
     * @return
     */
    public OsTypeEnum getOsType(){
        return OsTypeEnum.getMap().get(this.osType);
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

    public void writeFile(){
        try {
            FileHelper.writeFile(Constant.CONG_PATH, JSONObject.toJSONString(this));
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

    public String getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(String isMaster) {
        this.isMaster = isMaster;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public Boolean isStandBy() {
        return StringUtils.equals(isStandBy, "1");
    }

    public void setIsStandBy(String isStandBy) {
        this.isStandBy = isStandBy;
    }

    public String getPublishPath() {
        if(StringUtils.isBlank(this.publishPath)){
            this.publishPath = "http://" + this.localIp + ":" +this.port + "/publish/callback";
        }
        return publishPath;
    }

    public void setPublishPath(String publishPath) {
        this.publishPath = publishPath;
    }

    public String getLocalServices() {
        return localServices;
    }

    public void setLocalServices(String localServices) {
        this.localServices = localServices;
    }
}
