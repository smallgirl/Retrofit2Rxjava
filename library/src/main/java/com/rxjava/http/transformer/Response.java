package com.rxjava.http.transformer;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * Response
 */

public class Response<T> {

    private int code;
    private String msg;

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

    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}