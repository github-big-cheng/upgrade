package com.dounion.server.core.exception;

import com.dounion.server.core.base.BaseException;
import com.dounion.server.core.helper.StringHelper;

public class BusinessException extends BaseException {

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    public BusinessException(String msg, String...params) {
        this(StringHelper.parse1(msg, params));
    }

}
