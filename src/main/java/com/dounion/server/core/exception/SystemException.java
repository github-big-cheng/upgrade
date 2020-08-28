package com.dounion.server.core.exception;

import com.dounion.server.core.base.BaseException;

public class SystemException extends BaseException {

    public SystemException() {
    }

    public SystemException(String msg) {
        super(msg);
    }

    public SystemException(int code, String msg) {
        super(code, msg);
    }

    private int code;
    private String msg;



}
