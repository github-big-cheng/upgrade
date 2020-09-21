package com.dounion.server.core.route;


import com.dounion.server.core.base.Constant;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.entity.DownloadRouteRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
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

    private static Object LOCK1 = new Object();
    private static Object LOCK2 = new Object();
    private static Object LOCK3 = new Object();

    /**
     * 最大同时下载数
     */
    final static Integer MAX_COUNT = ConfigurationHelper.getInt(Constant.CONF_DOWNLOAD_MAX_COUNT, -1);
    /**
     * 注册路由最大活动时间
     */
    final static Long MAX_ROUTE_TIME = ConfigurationHelper.getLong(Constant.CONF_DOWNLOAD_MAX_ROUTE_TIME, -1);
    /**
     * 下载速率系数
     */
    final static Long DOWNLOAD_SPEED_RATIO = ConfigurationHelper.getLong(Constant.CONF_DOWNLOAD_SPEED_RATIO, 0);


    /**
     * 本地下载数
     */
    public final static ConcurrentHashMap<String, AtomicInteger>
            LOCAL_COUNTER_MAP = new ConcurrentHashMap<>();

    /**
     * 已注册的路由表
     *      key:path
     *      value:routes
     */
    public final static ConcurrentHashMap<String, List<DownloadRouteRecord>>
            ROUTE_INFO_MAP = new ConcurrentHashMap<>();


    /**
     * 路由队列集合
     *  key:path
     *  value:queue
     */
    public final static Map<String, BlockingQueue<DownloadRouteRecord>> ROUTE_QUEUE_MAP = new HashMap<>();


    // ================================================ local count  ===================================================

    /**
     * 获取本地指定下载资源的下载数AtomicInteger对象
     *
     * @param path
     * @return
     */
    private static AtomicInteger get(String path) {
        AtomicInteger integer = LOCAL_COUNTER_MAP.get(path);
        if (integer == null) {
            synchronized (LOCK1) {
                if (integer == null) {
                    integer = new AtomicInteger(0);
                    LOCAL_COUNTER_MAP.put(path, integer);
                }
                integer = LOCAL_COUNTER_MAP.get(path);
            }
        }
        return integer;
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
     * @param path
     * @param record
     */
    public static void routeRegister(String path, DownloadRouteRecord record) {
        List<DownloadRouteRecord> records = ROUTE_INFO_MAP.get(path);
        if(records == null){
            synchronized (LOCK2) {
                if(records == null){
                    records = Collections.synchronizedList(new LinkedList<DownloadRouteRecord>());
                    ROUTE_INFO_MAP.put(path, records);
                }
                records = ROUTE_INFO_MAP.get(path);
            }
        }

        records.add(record);
    }


    /**
     * 按下载路径注销路由注册表
     * @param path
     */
    public static void routeCancel(String path) {
        Assert.notNull(path, "path is required");
        logger.debug("RouteHandler.routeCancel path is:{}", path);
        ROUTE_INFO_MAP.remove(path);
    }

    /**
     * 按下载路径+主机注销路由注册表
     * @param path
     */
    public static void routeCancel(String path, String host) {
        Assert.notNull(path, "path is required");
        Assert.notNull(host, "host is required");
        logger.debug("RouteHandler.routeCancel path is:{}, host is {}", path, host);

        List<DownloadRouteRecord> records = ROUTE_INFO_MAP.get(path);
        if(CollectionUtils.isEmpty(records)){
            return;
        }

        int removeInx = -1;
        for(int i=0; i<records.size(); i++){
            if(StringUtils.equals(records.get(i).getHost(), host)){
                removeInx = i;
                break;
            }
        }

        if(removeInx > -1){
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
        logger.debug("RouteHandler.getNewUrl getCount is {}", getCount(url));
        if (getCount(url) < MAX_COUNT) {
            return null;
        }

        return routeQueueOperation(url);
    }

    /**
     * 路由处理
     * @param url
     * @return
     */
    private static String routeQueueOperation(String url) {

        logger.debug("RouteHandler.routeQueueOperation url is : {}", url);
        BlockingQueue<DownloadRouteRecord> queue = ROUTE_QUEUE_MAP.get(url);
        logger.debug("RouteHandler.routeQueueOperation queue is null : {}", queue==null);

        // queue 为 null 初始化数据
        if(queue == null){
            synchronized (LOCK3) {
                if(queue == null){
                    queue = new LinkedBlockingQueue<>();
                    ROUTE_QUEUE_MAP.put(url, queue);
                }
                queue = ROUTE_QUEUE_MAP.get(url);
            }

            // 遍历添加队列
            List<DownloadRouteRecord> records = ROUTE_INFO_MAP.get(url);
            if(CollectionUtils.isEmpty(records)){
                logger.debug("RouteHandler.routeQueueOperation no records found");
                return null;
            }
            logger.debug("RouteHandler.routeQueueOperation records's size is {}", records.size());
            for(DownloadRouteRecord route : records){
                // 判断最大路由活动时间
                if(MAX_ROUTE_TIME > 0 && System.currentTimeMillis()-route.getRegisterTime()<MAX_ROUTE_TIME){
                    continue;
                }
                queue.add(route);
            }
        }

        DownloadRouteRecord record = queue.poll();
        logger.debug("RouteHandler.routeQueueOperation record is null {}", record==null);
        if(record == null){
            return null;
        }

        if(MAX_ROUTE_TIME>0 && System.currentTimeMillis()-MAX_ROUTE_TIME<=0){
            // 当前取出记录已过期，重新获取
            return routeQueueOperation(url);
        }

        logger.debug("RouteHandler.routeQueueOperation record's download path is {}", record.getDownloadPath());
        return record.getDownloadPath();
    }


    /**
     * 发布策略控制
     * @param index
     * @param fileLength
     * @param url 下载路径
     */
    public static void publishPolicy(Integer index, Long fileLength, String url){

        if(index==null || fileLength==null || StringUtils.isBlank(url)){
            logger.warn("publishPolicy: some parameters is null [index={},fileLength={},url={}]",
                    index, fileLength, url);
            return;
        }

        if(MAX_COUNT <= 0){
            // 未配置或不限制最大下载数量时,不做处理
            return;
        }

        if(DOWNLOAD_SPEED_RATIO <= 0){
            // 未配置下载系数,不做处理
            return;
        }

        // 统计当前可以提供下载的路由数量
        int routeCount = 0;
        List<DownloadRouteRecord> routeRecords = ROUTE_INFO_MAP.get(url);
        if(!CollectionUtils.isEmpty(routeRecords)){
            logger.debug("routeRecords size is {}", routeRecords.size());
            routeCount = routeRecords.size();
        }
        routeCount = MAX_COUNT + routeCount; // 当前路由数量 + 最大下载数

        // 超过最大下载次数的推送
        if(index!=0 && index%routeCount==0){
            fileLength = fileLength == null ? 0 : fileLength;
            // 根据文件大小及下载系数计算大致下载时间
            // 下载时间(秒) = 文件大小(byte)/1024(byte)/下载速率(Kb/s)
            long costTime = fileLength/1024/DOWNLOAD_SPEED_RATIO;
            try {
                logger.debug("publishPolicy sleep time is {} s", costTime);
                Thread.sleep(costTime*1000);
            } catch (InterruptedException e) {
                logger.error("publishPolicy error:{}", e);
            }
        }

    }
}
