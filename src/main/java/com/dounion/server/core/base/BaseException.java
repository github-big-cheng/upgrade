package com.dounion.server.core.base;

public class BaseException extends RuntimeException{

    protected int code;
    protected String msg;

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
        this.code = -1;
        this.msg = msg;
    }

    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
