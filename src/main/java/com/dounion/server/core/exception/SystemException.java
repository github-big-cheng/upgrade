package com.dounion.server.core.exception;

import com.dounion.server.core.base.BaseException;
import com.dounion.server.core.helper.StringHelper;

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

    public SystemException(String msg, String...params) {
        this(StringHelper.parse1(msg, params));
    }


}
