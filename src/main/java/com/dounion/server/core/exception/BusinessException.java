package com.dounion.server.core.exception;

import com.dounion.server.core.base.BaseException;

public class BusinessException extends BaseException {

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(int code, String msg) {
        super(code, msg);
    }

}
