package com.dounion.server.core.netty.client;

import com.dounion.server.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NettyResponse<V> {

    private final static Logger logger = LoggerFactory.getLogger(NettyResponse.class);

    /**
     * 超时时间 30s
     */
    private final static long TIME_OUT = 30 * 1000l;

    public NettyResponse() {
        this(TIME_OUT);
    }

    public NettyResponse(long timeout) {
        this.status = 0;
        this.timeout = timeout;
        this.LOCK = new ReentrantLock(true);
        this.condition = LOCK.newCondition();
    }

    private int status;
    private V v;
    private Throwable throwable;
    private long timeout;
    private final ReentrantLock LOCK;
    private final Condition condition;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    private Boolean isDone(){
        return this.status != 0;
    }


    public void setResult(int code, V v, Throwable throwable){
        LOCK.lock();
        try {
            this.status = code;
            this.v = v;
            this.throwable = throwable;
            condition.signalAll();
        } catch (Exception e) {
            throw e;
        } finally {
            LOCK.unlock();
        }
    }

    public void setSuccess(V v){
        int code = 200;
        if(v == null){
            code = 500;
        }
        this.setResult(code, v, null);
    }


    public void setError(int code, Throwable throwable){
        this.setResult(code, null, throwable);
    }

    public void setError(Throwable throwable){
       setError(500, throwable);
    }

    public V get(){
        if(!isDone()){
            Long start = System.currentTimeMillis();
            LOCK.lock();
            try {
                while(!isDone()) {
                    condition.await(1000, TimeUnit.MILLISECONDS);
                    if(isDone() || System.currentTimeMillis()-start > timeout){
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("NettyResponse get error: {}", e);
                return null;
            } finally {
                LOCK.unlock();
            }

            if(!isDone()){
                this.status = 502;
                this.throwable = new BusinessException("Netty request timeout");
                return null;
            }
        }

        return v;
    }

}
