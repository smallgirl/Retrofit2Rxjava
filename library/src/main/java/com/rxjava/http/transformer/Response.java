package com.rxjava.http.transformer;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * Response
 */

public class Response<T> {

    private boolean success;

    public int errorCode;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private T data;

}