package com.rxjava.http.exception;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 服务器异常
 */


public class ServerException extends RuntimeException {
    // 异常处理，为速度，不必要设置getter和setter
    public int code;
    public String message;
    public String response;

    public ServerException(int code, String message, String response) {
        super(message);
        this.code = code;
        this.message = message;
        this.response = response;
    }
}
