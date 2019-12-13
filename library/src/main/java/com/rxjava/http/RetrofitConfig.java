package com.rxjava.http;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.rxjava.http.interceptor.HeaderInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private boolean showLog;
    private int readTimeout;
    private int connectTimeout;
    private int writeTimeout;
    private String baseUrl;

    private Converter.Factory factory;

    public static final int DEFAULT_TIMEOUT = 20;

    public static final int READ_TIMEOUT = 20;

    private Map<String, Object> headerMaps;

    private List<Interceptor> interceptorList;

    public RetrofitConfig showLog(boolean showLog) {
        this.showLog = showLog;
        return this;
    }

    public RetrofitConfig readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RetrofitConfig connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public RetrofitConfig writeTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public RetrofitConfig baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RetrofitConfig factory(Converter.Factory factory) {
        this.factory = factory;
        return this;
    }

    public RetrofitConfig headerMaps(Map<String, Object> headerMaps) {
        this.headerMaps = headerMaps;
        return this;
    }

    public RetrofitConfig addInterceptor(Interceptor interceptor) {
        if (null == interceptorList) {
            interceptorList = new ArrayList<>();
        }
        interceptorList.add(interceptor);
        return this;
    }

    OkHttpClient createOkHttpClient() {
        return createOkHttpClient(showLog);
    }

    public OkHttpClient createOkHttpClient(boolean showLog) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        if (showLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.e("RetrofitClient", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }
        if (null != headerMaps) {
            okHttpBuilder.addInterceptor(new HeaderInterceptor(headerMaps));
        }
        if (null != interceptorList) {
            for (Interceptor interceptor : interceptorList) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                X509TrustManager trustManager = TLSSocketFactory.systemDefaultTrustManager();
                SSLSocketFactory sslSocketFactory = TLSSocketFactory.sslSocketFactory(trustManager);
                okHttpBuilder.sslSocketFactory(sslSocketFactory, trustManager);
            } catch (Exception exc) {
            }
        }
        return okHttpBuilder
                .addInterceptor(new HeaderInterceptor(headerMaps))
                .readTimeout(readTimeout > 0 ? readTimeout : READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout > 0 ? writeTimeout : READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout > 0 ? connectTimeout : DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public <K> K createApiService(Class<K> cls) {

        Retrofit retrofit = new Retrofit.Builder()
                .client(createOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(null != factory ? factory : GsonConverterFactory.create())
                .baseUrl(TextUtils.isEmpty(baseUrl) ? RetrofitClient.baseUrl : baseUrl)
                .build();

        return retrofit.create(cls);
    }

}
