package com.dounion.server.core.download;


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


}
