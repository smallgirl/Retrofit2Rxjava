package com.rxjava.http.exception;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 约定异常
 */

public interface ErrorType {
    /**
     * 正常
     */
    int SUCCESS = 0;
    /**
     * 未知错误
     */
    int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    int PARSE_ERROR = 1001;
    /**
     * 网络错误
     */
    int NETWORD_ERROR = 1002;
    /**
     * 协议出错 404 500 等
     */
    int HTTP_ERROR = 1003;
}
