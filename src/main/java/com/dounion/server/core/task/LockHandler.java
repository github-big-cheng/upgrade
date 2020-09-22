package com.dounion.server.core.task;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockHandler {

    private final static String JOIN_KEY = "#";
    public final static ConcurrentHashMap<String, ReentrantLock> REENTRANT_LOCK_MAP = new ConcurrentHashMap<>();
    public final static ConcurrentHashMap<String, Condition> CONDITION_MAP = new ConcurrentHashMap<>();


    /**
     * 获取重入锁对象
     * @param lockKey
     * @return
     */
    public static ReentrantLock getLock(String lockKey){

        Assert.notNull(lockKey, "lock key is required");

        ReentrantLock lock = REENTRANT_LOCK_MAP.get(lockKey);
        if(lock == null){
            REENTRANT_LOCK_MAP.putIfAbsent(lockKey, new ReentrantLock(true));
        }
        return REENTRANT_LOCK_MAP.get(lockKey);
    }

    /**
     * 获取
     * @param lockKey 锁名称
     * @param conditionKey 条件名称
     * @return
     */
    public static Condition newCondition(String lockKey, String conditionKey){
        String realKey = lockKey + JOIN_KEY + conditionKey;
        if(CONDITION_MAP.get(realKey) == null){
            ReentrantLock lock = getLock(lockKey);
            CONDITION_MAP.putIfAbsent(realKey, lock.newCondition());
        }
        return CONDITION_MAP.get(realKey);
    }


    /**
     * 锁
     * @param lockKey
     */
    public static void lock(String lockKey) {
        getLock(lockKey).lock();
    }

    /**
     * 解锁
     * @param lockKey
     */
    public static void unlock(String lockKey) {

        ReentrantLock lock = REENTRANT_LOCK_MAP.get(lockKey);
        if(lock != null){
            lock.unlock();
        }

        // Is it unsafe here ?
        if(!lock.isLocked() && lock.getHoldCount()==0 && lock.getQueueLength()==0){
            REENTRANT_LOCK_MAP.remove(lockKey);
            for(String key : CONDITION_MAP.keySet()){
                if(StringUtils.startsWith(key, lockKey + JOIN_KEY)){
                    CONDITION_MAP.remove(key);
                }
            }
        }
    }


}
