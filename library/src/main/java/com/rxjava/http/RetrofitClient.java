package com.rxjava.http;


import android.text.TextUtils;
import android.util.Log;

import com.rxjava.http.upload.UpLoadProgressInterceptor;
import com.rxjava.http.upload.UploadFileApi;
import com.rxjava.http.upload.UploadListener;
import com.rxjava.http.download.DownloadRetrofit;
import com.rxjava.http.gsonconverter.CustomGoonConvertFactory;
import com.rxjava.http.interceptor.HeaderInterceptor;
import com.rxjava.http.transformer.Transformer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 *
 */
public class RetrofitClient {

    private static final int DEFAULT_TIMEOUT = 20;

    private static final int READ_TIMEOUT = 20;

    private static final String BASE_URL = "http://service.jd100.com/cgi-bin/phone/";

    /**
     * 取所有数据
     */
    private static Retrofit retrofit;

    /**
     * 只取data 数据
     */
    private static Retrofit retrofitData;

    private static RetrofitClient instance;

    private String baseUrl;

    private Map<String, Object> headerMaps = new HashMap<>();

    private boolean isShowLog = true;

    private long readTimeout;
    private long writeTimeout;
    private long connectTimeout;

    /**
     * 通用的全局请求
     * @param cls
     * @param <K>
     * @return
     */
    public static <K> K getApiService(Class<K> cls)  {
        return getApiService(cls,true);
    }


    /**
     * 通用的全局请求
     * @param cls
     * @param fromData
     * @param <K>
     * @return
     */
    public static <K> K getApiService(Class<K> cls,boolean fromData)  {

        if (fromData){
            if (retrofitData == null) {
                retrofitData = new Retrofit.Builder()
                        .client(getOkHttpClient())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(CustomGoonConvertFactory.create(true))
                        .baseUrl(BASE_URL)
                        .build();
            }
            return retrofitData.create(cls);
        }else {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .client(getOkHttpClient())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(CustomGoonConvertFactory.create(false))
                        .baseUrl(BASE_URL)
                        .build();
            }
            return retrofit.create(cls);
        }
    }

    private static OkHttpClient getOkHttpClient (){
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("RetrofitClient", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpBuilder.addInterceptor(loggingInterceptor);
        OkHttpClient okHttpClient = okhttpBuilder
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
        return  okHttpClient;
    }

    /**
     * 自定义的单个 请求
     * @param cls
     * @param <K>
     * @return
     */
    public  <K> K creatApiService(Class<K> cls)  {
        return creatApiService(cls,true);
    }


    /**
     * 自定义的单个 请求
     * @param cls
     * @param fromData
     * @param <K>
     * @return
     */
    public  <K> K creatApiService(Class<K> cls,boolean fromData)  {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        if (isShowLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.e("RetrofitClient", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(loggingInterceptor);
        }
        OkHttpClient okHttpClient = okhttpBuilder
                .addInterceptor(new HeaderInterceptor(headerMaps))
                .readTimeout(readTimeout > 0 ? readTimeout : READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout > 0 ? writeTimeout : READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout > 0 ? connectTimeout : DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(CustomGoonConvertFactory.create(fromData))
                .baseUrl(TextUtils.isEmpty(baseUrl)?BASE_URL:baseUrl)
                .build();
        // 重置
        readTimeout=0;
        writeTimeout=0;
        connectTimeout=0;
        isShowLog=false;

        return retrofit.create(cls);
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }

        }
        return instance;
    }
    public RetrofitClient baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RetrofitClient addHeaders(Map<String, Object> headerMaps) {
        this.headerMaps = headerMaps;
        return this;
    }

    public RetrofitClient showLog(boolean isShowLog) {
        this.isShowLog = isShowLog;
        return this;
    }


    public static Observable<ResponseBody> uploadImg(String uploadUrl, String filePath, UploadListener listener) {
        File file = new File(filePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        UpLoadProgressInterceptor interceptor = new UpLoadProgressInterceptor(listener);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .build();
        String baseUrl = "https://api.github.com/";
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(baseUrl)
                .build()
                .create(UploadFileApi.class)
                .uploadImg(uploadUrl, body)
                .compose(Transformer.<ResponseBody>switchSchedulers());
    }
    /**
     * 下载文件
     * @param fileUrl
     * @return
     */
    public static Observable<ResponseBody> downloadFile(String fileUrl) {
        return DownloadRetrofit.downloadFile(fileUrl);
    }
}