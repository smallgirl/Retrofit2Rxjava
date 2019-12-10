package com.rxjava.http.upload;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 文件上传拦截
 */

public class UpLoadProgressInterceptor implements Interceptor {
    private UploadListener mUploadListener;

    public UpLoadProgressInterceptor(UploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (null == request.body()) {
            return chain.proceed(request);
        }

        Request build = request.newBuilder()
                .method(request.method(),
                        new CountingRequestBody(request.body(),
                                mUploadListener))
                .build();
        return chain.proceed(build);
    }
}