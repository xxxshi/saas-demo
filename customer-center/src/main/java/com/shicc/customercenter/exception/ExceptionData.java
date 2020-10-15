package com.shicc.customercenter.exception;
;

import java.io.Serializable;

public class ExceptionData implements Serializable {
    private int errorCode;
    private String message;

    public ExceptionData(int errorCode, String msg) {
        this.errorCode = errorCode;
        this.message = msg;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
