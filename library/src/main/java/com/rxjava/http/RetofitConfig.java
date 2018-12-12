package com.rxjava.http;

import android.text.TextUtils;
import android.util.Log;

import com.rxjava.http.interceptor.HeaderInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetofitConfig {

    private boolean showLog;
    private int readTimeout;
    private int connectTimeout;
    private int writeTimeout;
    private String baseUrl;

    private Converter.Factory factory;

    public static final int DEFAULT_TIMEOUT = 20;

    public static final int READ_TIMEOUT = 20;


    private Map<String, Object> headerMaps;

    public RetofitConfig showLog(boolean showLog) {
        this.showLog = showLog;
        return this;
    }

    public RetofitConfig readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RetofitConfig connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public RetofitConfig writeTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public RetofitConfig baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RetofitConfig factory(Converter.Factory factory) {
        this.factory = factory;
        return this;
    }

    public RetofitConfig headerMaps(Map<String, Object> headerMaps) {
        this.headerMaps = headerMaps;
        return this;
    }
    public  <K> K creatApiService(Class<K> cls)  {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        if (showLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.e("RetrofitClient", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(loggingInterceptor);
        }
        if (null!=headerMaps){
            okhttpBuilder.addInterceptor(new HeaderInterceptor(headerMaps));
        }
        okhttpBuilder
                .addInterceptor(new HeaderInterceptor(headerMaps))
                .readTimeout(readTimeout > 0 ? readTimeout : READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout > 0 ? writeTimeout : READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout > 0 ? connectTimeout : DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        OkHttpClient okHttpClient =okhttpBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(null != factory ? factory : GsonConverterFactory.create())
                .baseUrl(TextUtils.isEmpty(baseUrl)?RetrofitClient.baseUrl:baseUrl)
                .build();

        return retrofit.create(cls);
    }

}
