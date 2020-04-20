package com.rxjava.http.exception;

import android.util.Log;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.text.ParseException;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import retrofit2.HttpException;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 异常处理
 */

public class ExceptionEngine {

    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ApiException handleException(Throwable throwable) {
        Log.e("handleException", throwable.toString());
        ApiException ex;
        if (throwable instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) throwable;
            ex = new ApiException(throwable, ErrorType.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    ex.msg = "登录过期";
                    handTokenInvalid();
                    break;
                default:
                    ex.msg = "服务器错误，请稍后重试";  //其它均视为网络错误
                    break;
            }
        } else if (throwable instanceof ServerException) {    //服务器返回的错误
            ServerException resultException = (ServerException) throwable;
            ex = new ApiException(resultException, resultException.code);
            ex.msg = resultException.msg;
            ex.response = resultException.response;
        } if (throwable instanceof SocketTimeoutException) {
            ex = new ApiException(throwable, ErrorType.NETWORK_ERROR);
            ex.msg = "网络异常，服务器响应超时";
        } else if (throwable instanceof ConnectException) {
            ex = new ApiException(throwable, ErrorType.NETWORK_ERROR);
            ex.msg = "网络异常，请检查网络后重试";
        } else if (throwable instanceof UnknownHostException || throwable instanceof UnknownServiceException || throwable instanceof IOException) {
            ex = new ApiException(throwable, ErrorType.NETWORK_ERROR);
            ex.msg = "网络异常，请检查网络后重试";
            if (throwable instanceof SSLPeerUnverifiedException || throwable instanceof SSLHandshakeException) {
                ex.msg = "网络异常，https证书异常";
            }
        } else if (throwable instanceof JsonParseException
                || throwable instanceof JSONException
                || throwable instanceof ParseException) {
            ex = new ApiException(throwable, ErrorType.PARSE_ERROR);
            ex.msg = "解析错误";
        } else if (throwable instanceof RuntimeException) {
            ex = new ApiException(throwable, ErrorType.RUN_TIME);
            ex.msg = "很抱歉,程序运行出现了错误";
        } else {
            ex = new ApiException(throwable, ErrorType.UNKNOWN);
            ex.msg = "未知错误";
        }
        return ex;
    }

    private static void handTokenInvalid() {
//        Intent intent = new Intent();
//        intent.setClass(BaseApplication.getInstance(), RetrofitClient.tokenInvalidClass);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        BaseApplication.getInstance().startActivity(intent);
    }

}