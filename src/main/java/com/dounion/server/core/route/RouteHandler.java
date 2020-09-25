package com.dounion.server.core.route;


import com.dounion.server.core.base.Constant;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.helper.DataHelper;
import com.dounion.server.entity.DownloadRouteRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 资源下载分发控制器
 */
public class RouteHandler {

    private final static Logger logger = LoggerFactory.getLogger(RouteHandler.class);

    // private constructor
    private RouteHandler() {
    }

    private static Object LOCK3 = new Object();

    /**
     * 最大同时下载数
     */
    final static Integer MAX_COUNT =
            ConfigurationHelper.getInt(Constant.CONF_DOWNLOAD_MAX_COUNT, -1);
    /**
     * 注册路由最大活动时间
     */
    final static Long MAX_ROUTE_TIME =
            ConfigurationHelper.getLong(Constant.CONF_DOWNLOAD_MAX_ROUTE_TIME, -1);
    /**
     * 下载速率系数
     */
    public final static Long DOWNLOAD_SPEED_RATIO =
            ConfigurationHelper.getLong(Constant.CONF_DOWNLOAD_SPEED_RATIO, 0);


    /**
     * 本地下载数
     */
    public final static ConcurrentHashMap<String, AtomicInteger>
            LOCAL_COUNTER_MAP = new ConcurrentHashMap<>();

    /**
     * 已注册的路由表
     * key:path
     * value:routes
     */
    public final static ConcurrentHashMap<String, List<DownloadRouteRecord>>
            ROUTE_INFO_MAP = new ConcurrentHashMap<>();


    /**
     * 路由队列集合
     * key:path
     * value:queue
     */
    public final static ConcurrentHashMap<String, BlockingQueue<DownloadRouteRecord>> ROUTE_QUEUE_MAP = new ConcurrentHashMap<>();


    // ================================================ local count  ===================================================

    /**
     * 获取本地指定下载资源的下载数AtomicInteger对象
     *
     * @param path
     * @return
     */
    private static AtomicInteger get(String path) {
        if (LOCAL_COUNTER_MAP.get(path) == null) {
            LOCAL_COUNTER_MAP.putIfAbsent(path, new AtomicInteger(0));
        }
        return LOCAL_COUNTER_MAP.get(path);
    }


    /**
     * 获取本地资源当前下载数
     *
     * @param path
     * @return
     */
    public static Integer getCount(String path) {
        return get(path).get();
    }


    /**
     * 本地资源 计数+1
     *
     * @param path
     * @return
     */
    public static Integer countDown(String path) {
        logger.trace("RouteHandler.countDown path is:{}, count is :{}", path, get(path));
        return get(path).addAndGet(1);
    }


    /**
     * 本地资源 计数-1
     *
     * @param path
     * @return
     */
    public static Integer reduction(String path) {
        return get(path).decrementAndGet();
    }


    // ================================================ route operation ================================================


    /**
     * 路由注册
     *
     * @param path
     * @param record
     */
    public static void routeRegister(String path, DownloadRouteRecord record) {
        if (ROUTE_INFO_MAP.get(path) == null) {
            ROUTE_INFO_MAP.putIfAbsent(path, Collections.synchronizedList(new LinkedList<DownloadRouteRecord>()));
        }
        ROUTE_INFO_MAP.get(path).add(record);
    }


    /**
     * 按下载路径注销路由注册表
     *
     * @param path
     */
    public static void routeCancel(String path) {
        Assert.notNull(path, "path is required");
        logger.debug("RouteHandler.routeCancel path is:{}", path);
        ROUTE_INFO_MAP.remove(path);
    }

    /**
     * 按下载路径+主机注销路由注册表
     *
     * @param path
     */
    public static void routeCancel(String path, String host) {
        Assert.notNull(path, "path is required");
        Assert.notNull(host, "host is required");
        logger.debug("RouteHandler.routeCancel path is:{}, host is {}", path, host);

        List<DownloadRouteRecord> records = ROUTE_INFO_MAP.get(path);
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        int removeInx = -1;
        for (int i = 0; i < records.size(); i++) {
            if (StringUtils.equals(records.get(i).getHost(), host)) {
                removeInx = i;
                break;
            }
        }

        if (removeInx > -1) {
            records.remove(removeInx);
        }
    }

    // ================================================ for download operation =========================================


    /**
     * 判断是否需要新的下载路由
     *
     * @param url
     * @return
     */
    public static String getNewUrl(String url) {

        if (MAX_COUNT <= 0) {
            // 小于等于0为不限制
            return null;
        }

        // 当前下载数未超过最大限制
        logger.trace("RouteHandler.getNewUrl getCount is {}", getCount(url));
        if (getCount(url) < MAX_COUNT) {
            return null;
        }

        return routeQueueOperation(url);
    }

    /**
     * 路由处理
     *
     * @param url
     * @return
     */
    private static String routeQueueOperation(String url) {

        logger.trace("RouteHandler.routeQueueOperation url is : {}", url);
        BlockingQueue<DownloadRouteRecord> queue = ROUTE_QUEUE_MAP.get(url);
        logger.trace("RouteHandler.routeQueueOperation queue is null : {}", queue == null);

        // queue 为 null 初始化数据
        if (CollectionUtils.isEmpty(queue)) {
            if (queue == null) { // 初始化
                ROUTE_QUEUE_MAP.putIfAbsent(url, new LinkedBlockingQueue<DownloadRouteRecord>());
            }
            queue = ROUTE_QUEUE_MAP.get(url);


            // 遍历添加队列
            List<DownloadRouteRecord> records = ROUTE_INFO_MAP.get(url);
            if (CollectionUtils.isEmpty(records)) {
                logger.trace("RouteHandler.routeQueueOperation no records found");
                return null;
            }
            logger.trace("RouteHandler.routeQueueOperation records's size is {}", records.size());
            for (DownloadRouteRecord route : records) {
                // 判断最大路由活动时间
                if (MAX_ROUTE_TIME > 0) {
                    long activeTime = System.currentTimeMillis() - MAX_ROUTE_TIME;
                    if (activeTime > route.getRegisterTime()) {
                        logger.debug("record is expired, url :{}, activeTime is {}, registerTime is {}",
                                route.getDownloadPath(), activeTime, route.getRegisterTime());
                        continue;
                    }
                }
                queue.add(route);
            }
        }

        DownloadRouteRecord record = queue.poll();
        logger.trace("RouteHandler.routeQueueOperation record is null {}", record == null);
        if (record == null) {
            return null;
        }

        if (MAX_ROUTE_TIME > 0 && System.currentTimeMillis() - MAX_ROUTE_TIME <= 0) {
            // 当前取出记录已过期，重新获取
            return routeQueueOperation(url);
        }

        logger.trace("RouteHandler.routeQueueOperation record's download path is {}", record.getDownloadPath());
        return record.getDownloadPath();
    }


    /**
     * 发布策略控制
     *
     * @param index
     * @param fileLength
     * @param url        下载路径
     */
    public static void publishPolicy(Integer index, Long fileLength, String url) {

        if (index == null || fileLength == null || StringUtils.isBlank(url)) {
            logger.warn("publishPolicy: some parameters is null [index={},fileLength={},url={}]",
                    index, fileLength, url);
            return;
        }

        if (MAX_COUNT <= 0) {
            // 未配置或不限制最大下载数量时,不做处理
            return;
        }

        if (DOWNLOAD_SPEED_RATIO <= 0) {
            // 未配置下载系数,不做处理
            return;
        }

        // 统计当前可以提供下载的路由数量
        int routeCount = 0;
        List<DownloadRouteRecord> routeRecords = ROUTE_INFO_MAP.get(url);
        if (!CollectionUtils.isEmpty(routeRecords)) {
            logger.trace("routeRecords size is {}", routeRecords.size());
            routeCount = routeRecords.size();
        }
        routeCount = MAX_COUNT + routeCount; // 当前路由数量 + 最大下载数

        // 超过最大下载次数的推送
        if (index > 1 && index % routeCount == 0) {
            fileLength = fileLength == null ? 0 : fileLength;
            // 根据文件大小及下载系数计算大致下载时间
            // 下载时间(秒) = 文件大小(byte)/1024(byte)/下载速率(Kb/s)
            long costTime = fileLength / 1024 / DOWNLOAD_SPEED_RATIO;
            try {
                logger.trace("publishPolicy sleep time is {} s", costTime);
                Thread.sleep(costTime * 1000);
            } catch (InterruptedException e) {
                logger.error("publishPolicy error:{}", e);
            }
        }

    }


    // ================================================ for download progress ==========================================

    static class DownloadProgress {

        public DownloadProgress(String name, long fileSize) {
            this(name, fileSize, null, null);
        }

        public DownloadProgress(String name, long fileSize, String local, String remote) {
            this.name = name;
            this.fileSize = fileSize;
            this.local = local;
            this.remote = remote;
            this.start = System.currentTimeMillis();
        }

        private String name; // 下载名称
        private String local; // 本地对象
        private String remote; // 下载对象
        private long downloadSize; // 已下载大小
        private long fileSize; // 文件大小
        private long start; // 开始时间
        private long end; // 结束时间

        /**
         * 下载进度
         * @return
         */
        public BigDecimal getProgress(){
            return DataHelper.percent(this.fileSize, this.downloadSize);
        }

        /**
         * 下载速率 Kb/s
         * @return
         */
        public BigDecimal getSpeed(){
            long currentTime = (System.currentTimeMillis() - this.start) / 1000;
            return DataHelper.divide(this.downloadSize/1024, currentTime);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocal() {
            return local;
        }

        public void setLocal(String local) {
            this.local = local;
        }

        public String getRemote() {
            return remote;
        }

        public long getDownloadSize() {
            return downloadSize;
        }

        public void setDownloadSize(long downloadSize) {
            this.downloadSize = downloadSize;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public void setRemote(String remote) {
            this.remote = remote;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }
    }

    /**
     * 下载进度集合
     *      key:path
     *      value:Channel
     */
    public final static ConcurrentHashMap<String, ConcurrentHashMap<String, DownloadProgress>>
            DOWNLOAD_PROGRESS_MAP = new ConcurrentHashMap<>();


    /**
     * 下载进度注册
     * @param path
     * @param fileSize
     * @param local
     * @param remote
     */
    public static void progressRegister(String path, long fileSize, String local, String remote){
        if(!DOWNLOAD_PROGRESS_MAP.contains(path)){
            DOWNLOAD_PROGRESS_MAP.putIfAbsent(path, new ConcurrentHashMap<String, DownloadProgress>());
        }

        if(!DOWNLOAD_PROGRESS_MAP.get(path).contains(remote)){
            DOWNLOAD_PROGRESS_MAP.get(path).putIfAbsent(remote, new DownloadProgress(path, fileSize, local, remote));
        }
    }


    /**
     * 更新下载进度
     * @param path
     * @param remote
     * @param downloadSize 已下载文件大小
     */
    public static void progressRefresh(String path, String remote, long downloadSize){
        ConcurrentHashMap<String, DownloadProgress> map = DOWNLOAD_PROGRESS_MAP.get(path);
        if(CollectionUtils.isEmpty(map)){
            return;
        }

        DownloadProgress progress = map.get(remote);
        if(progress == null){
            return;
        }
        progress.setDownloadSize(downloadSize);
    }


    /**
     * 下载进度注销
     * @param path
     * @param remote
     */
    public static void progressCancel(String path, String remote){
        ConcurrentHashMap<String, DownloadProgress> map = DOWNLOAD_PROGRESS_MAP.get(path);
        if(CollectionUtils.isEmpty(map)){
            return;
        }
        map.remove(remote);
    }
}
