package com.rxjava.http;


import com.rxjava.http.gsonconverter.CustomGoonConvertFactory;
import com.rxjava.http.upload.UpLoadProgressInterceptor;
import com.rxjava.http.upload.UploadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    private static Retrofit retrofit;
    private static boolean showLog = true;
    protected static String baseUrl = "http://service.jd100.com/cgi-bin/phone/";;
    protected static List<Interceptor> defaultInterceptors = new ArrayList<>();
    public static Class tokenInvalidClass;

    public static void addDefaultInterceptors(Interceptor interceptor) {
        defaultInterceptors.add(interceptor);
    }

    public static void setShowLog(boolean showLog) {
        RetrofitClient.showLog = showLog;
    }

    public static void setBaseUrl(String baseUrl) {
        RetrofitClient.baseUrl = baseUrl;
    }

    /**
     * 通用的全局请求
     *
     * @param cls
     * @param <K>
     * @return
     */
    public static <K> K getApiService(Class<K> cls) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(new RetrofitConfig().createOkHttpClient(showLog))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(CustomGoonConvertFactory.create(true))
                    .baseUrl(baseUrl)
                    .build();
        }
        return retrofit.create(cls);
    }


    public static RetrofitConfig newRetrofit() {
        return new RetrofitConfig().showLog(true);
    }


    // https://blog.csdn.net/huweijian5/article/details/52610093
    // https://blog.csdn.net/u012391876/article/details/52606817

    public static List<MultipartBody.Part> getUploadParam(Map<String, Object> mapParam) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (null != mapParam && mapParam.size() > 0) {
            for (Map.Entry<String, Object> entry : mapParam.entrySet()) {
                if (entry.getValue() instanceof File) {
                    if (((File) entry.getValue()).exists()) {
                        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), ((File) entry.getValue()));
                        builder.addFormDataPart(entry.getKey(), ((File) entry.getValue()).getName(), imageBody);
                    }
                } else {
                    builder.addFormDataPart(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        List<MultipartBody.Part> parts = builder.build().parts();
        return parts;
    }

    public static <K> K createUploadService(Class<K> cls, UploadListener listener) {

        return createUploadService(cls, listener, GsonConverterFactory.create());
    }

    public static <K> K createUploadService(Class<K> cls, UploadListener listener, Converter.Factory factory) {

        return new RetrofitConfig()
                .addInterceptor(new UpLoadProgressInterceptor(listener))
                .connectTimeout(20)
                .writeTimeout(20)
                .showLog(false)// log拦截器会导致 回调走2次 不要开启
                .factory(factory)
                .createApiService(cls);
    }

    /**
     * 下载文件
     *
     * @param cls
     * @param <K>
     * @return
     */
    public static <K> K createDownloadService(Class<K> cls) {
        Map<String, Object> headerMaps = new HashMap<>();
        headerMaps.put("Accept-Encoding", "identity");// 服务器文件进度
        OkHttpClient okHttpClient = new RetrofitConfig()
                .headerMaps(headerMaps)
                .connectTimeout(1000)
                .writeTimeout(1000)
                .createOkHttpClient();
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .build()
                .create(cls);
    }
}