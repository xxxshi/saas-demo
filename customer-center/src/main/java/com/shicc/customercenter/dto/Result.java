package com.shicc.customercenter.dto;

public class Result<T> {
    T result;
    boolean success = false;
    String code;
    String msg;

    public Result(T result, boolean status, String msg) {
        this.result = result;
        this.success = status;
        this.msg = msg;
    }

    public Result() {
    }

    public static <T> Result<T> newInstance(boolean success, T result, String msg) {
        Result<T> r = new Result<T>();
        r.setSuccess(success);
        r.setResult(result);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> newInstance() {
        return newInstance(null);
    }

    public static <T> Result<T> newInstance(T object) {
        return newInstance(object, null);
    }

    public static <T> Result<T> newInstance(T object, String msg) {
        return newInstance(true, object, msg);
    }

    public static <T> Result<T> newFailInstance(String msg) {
        return newFailInstance(null, msg);
    }

    public static <T> Result<T> newFailInstance(T object, String msg) {
        return newInstance(false, object, msg);
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean status) {
        this.success = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
