package com.dounion.server.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockHandler {


    private final static Logger logger = LoggerFactory.getLogger(LockHandler.class);

    private LockHandler(){
        this(false);
    }

    private LockHandler(boolean fair){
        this.lockPoint = 0;
        this.lock = new ReentrantLock(fair);
    }

    final private AtomicInteger lockedCount = new AtomicInteger(0);
    private ReentrantLock lock;
    private int lockPoint;

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
        lockedCount.incrementAndGet(); // 计数器自增
        this.lock.lock();
    }

    public void lock(int lockPoint){
        this.lockPoint = lockPoint;
        this.lock();
    }

    public boolean tryLock(){
        lockedCount.incrementAndGet();
        boolean flag = this.lock.tryLock();
        if(!flag){
            // 未锁上,还原
            lockedCount.decrementAndGet();
        }
        return flag;
    }

    public void unlock(){
        lockedCount.decrementAndGet(); // 计数器自减
        this.lock.unlock();
    }

    public boolean isRelease(){
        return lockedCount.intValue() == 0;
    }

    public AtomicInteger getLockedCount() {
        return lockedCount;
    }

    public int getLockPoint() {
        return lockPoint;
    }

    public void setLockPoint(int lockPoint) {
        this.lockPoint = lockPoint;
    }

    public final static ConcurrentHashMap<String, LockHandler> LOCK_HANDLER_MAP = new ConcurrentHashMap<>();




    /**
     * 获取锁控制对象
     * @param key
     * @return
     */
    public static synchronized LockHandler getHandler(String key, boolean fair) {
        if(!LOCK_HANDLER_MAP.contains(key)){
            LOCK_HANDLER_MAP.putIfAbsent(key, new LockHandler(fair));
        }
        return LOCK_HANDLER_MAP.get(key);
    }


    /**
     * 获取锁控制对象
     *      默认-公平锁
     * @param key
     * @return
     */
    public static LockHandler getHandler(String key) {
        return getHandler(key, true);
    }


    /**
     * 上锁
     * @param key
     */
    public static void lock(String key){
        getHandler(key).lock();
    }
    /**
     * 上锁
     * @param key
     */
    public static void lock(String key, int lockPoint){
        getHandler(key).lock(lockPoint);
    }

    /**
     * 上锁
     * @param key
     */
    public static boolean tryLock(String key){
        return getHandler(key).tryLock();
    }

    /**
     * 解锁
     * @param key
     */
    public static void unlock(String key){
        LockHandler handler = getHandler(key);
        handler.unlock();
        if(handler.isRelease()){
            release(key);
        }
    }


    /**
     * 释放指定的锁对象，避免内存溢出
     * @param key
     */
    public static void release(String key){
        LOCK_HANDLER_MAP.remove(key);
    }


    /**
     * 关闭锁资源
     */
    public static void shutdown() {
        LockHandler handler;
        for (String key : LOCK_HANDLER_MAP.keySet()) {
            handler = LOCK_HANDLER_MAP.get(key);
            while(handler.lock.isLocked()){
                logger.trace("handler:...{}", handler);
                handler.unlock();
            }
        }
        logger.trace("after shut down...{}", LOCK_HANDLER_MAP);
    }

}
