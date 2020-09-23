package com.dounion.server.core.task;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockHandler {

    final private AtomicInteger lockedCount = new AtomicInteger(0);
    final private ReentrantLock lock = new ReentrantLock(true);

    private ConcurrentHashMap<String, Condition> conditionMap = new ConcurrentHashMap<>();

    public ReentrantLock getLock(){
        return this.lock;
    }

    public Condition newCondition(String key){
        if(!conditionMap.contains(key)){
            conditionMap.putIfAbsent(key, lock.newCondition());
        }
        return conditionMap.get(key);
    }

    public void lock(){
        this.lock.lock();
        lockedCount.incrementAndGet(); // 计数器自增
    }

    public void unlock(){
        this.lock.unlock();
        lockedCount.decrementAndGet(); // 计数器自减
    }

    public boolean isRelease(){
        return lockedCount.intValue() == 0;
    }


    public final static ConcurrentHashMap<String, LockHandler> LOCK_HANDLER_MAP = new ConcurrentHashMap<>();


    /**
     * 获取锁控制对象
     * @param key
     * @return
     */
    public static synchronized LockHandler getHandler(String key) {
        if(!LOCK_HANDLER_MAP.contains(key)){
            LOCK_HANDLER_MAP.putIfAbsent(key, new LockHandler());
        }
        return LOCK_HANDLER_MAP.get(key);
    }

    /**
     * 获取重入锁
     * @param key
     * @return
     */
    public static synchronized ReentrantLock getLock(String key){
        return getHandler(key).getLock();
    }

    /**
     * 获取条件对象
     * @param lockKey
     * @param conditionKey
     * @return
     */
    public static synchronized Condition getCondition(String lockKey, String conditionKey){
        return getHandler(lockKey).newCondition(conditionKey);
    }


    /**
     * 上锁
     * @param key
     */
    public static void lock(String key){
        getHandler(key).lock();
    }

    /**
     * 解锁
     * @param key
     */
    public static void unlock(String key){
        LockHandler handler = getHandler(key);
        handler.unlock();
        if(handler.isRelease()){
            LOCK_HANDLER_MAP.remove(key);
        }
    }


}
