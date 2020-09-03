package com.dounion.server.core.download;


import com.dounion.server.core.helper.ConfigurationHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 资源下载分发控制器
 */
public class DownloadHandler {

    // private constructor
    private DownloadHandler(){ }

    private static Object LOCK = new Object();

    private final static ConcurrentHashMap<String, AtomicInteger> DOWNLOAD_MAP = new ConcurrentHashMap<>();


    private static AtomicInteger get(String path){
        AtomicInteger integer = DOWNLOAD_MAP.get(path);
        if(integer == null){
            synchronized (LOCK) {
                integer = DOWNLOAD_MAP.get(path);
                if(integer == null){
                    integer = new AtomicInteger(0);
                    DOWNLOAD_MAP.put(path, integer);
                }
            }
        }
        return integer;
    }


    /**
     * 获取当前下载数
     * @param path
     * @return
     */
    public static Integer getCount(String path) {
        return get(path).get();
    }


    /**
     * 计数+1
     * @param path
     * @return
     */
    public static Integer countDown(String path){
        return get(path).addAndGet(1);
    }


    /**
     * 计数-1
     * @param path
     * @return
     */
    public static Integer reduction(String path){
        return get(path).decrementAndGet();
    }


    /**
     * 判断是否需要新的下载路由
     * @param url
     * @return
     */
    public static String getNewUrl(String host, String url) {

        Integer maxCount = ConfigurationHelper.getInt("max_download_count", -1);
        if(maxCount <= 0){
            // 小于等于0为不限制
            return null;
        }

        // 当前下载数未超过最大限制
        if(getCount(url) < maxCount){
            return null;
        }

        return null;
    }
}
