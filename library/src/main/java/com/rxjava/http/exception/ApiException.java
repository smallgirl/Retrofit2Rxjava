package com.rxjava.http.exception;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 异常
 */

public class ApiException extends Exception {

    public int code;
    public String msg;
    public String response;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ApiException(int code, String msg, String response) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.response = response;
    }
}
