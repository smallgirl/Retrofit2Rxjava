package com.rxjava.http.exception;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 服务器异常
 */


public class ServerException extends RuntimeException {

    // 异常处理，为速度，不必要设置getter和setter
    public int code;
    public String msg;
    public String response;

    public ServerException(int code, String msg, String response) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.response = response;
    }
}
