package com.rxjava.http;


import android.util.Log;

import com.rxjava.http.download.DownloadRetrofit;
import com.rxjava.http.gsonconverter.CustomGoonConvertFactory;
import com.rxjava.http.upload.UpLoadProgressInterceptor;
import com.rxjava.http.upload.UploadListener;

import java.io.File;
import java.util.List;
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

import static com.rxjava.http.RetrofitConfig.DEFAULT_TIMEOUT;
import static com.rxjava.http.RetrofitConfig.READ_TIMEOUT;


public class RetrofitClient {

    private static Retrofit retrofit;
    private static boolean showLog;
    protected static String baseUrl = "http://service.jd100.com/cgi-bin/phone/";

    public static void setShowLog(boolean showLog) {
        RetrofitClient.showLog = showLog;
    }

    public static void setBaseUrl(String baseUrl) {
        RetrofitClient.baseUrl = baseUrl;
    }

    /**
     * 通用的全局请求
     * @param cls
     * @param <K>
     * @return
     */
    public static <K> K getApiService(Class<K> cls)  {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient(showLog))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    //.addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(CustomGoonConvertFactory.create(true))
                    .baseUrl(baseUrl)
                    .build();
        }
        return retrofit.create(cls);
    }
    public static RetrofitConfig newRetofit() {
        return new RetrofitConfig();
    }

    private static OkHttpClient getOkHttpClient(boolean isShowLog) {
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
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout( DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        return  okHttpClient;
    }

    // https://blog.csdn.net/huweijian5/article/details/52610093
    // https://blog.csdn.net/u012391876/article/details/52606817

    public static List<MultipartBody.Part> getUploadParam(Map<String,Object> mapParam){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (null!=mapParam && mapParam.size()>0){
            for (Map.Entry<String,Object> entry : mapParam.entrySet()) {
                if (entry.getValue() instanceof File){
                    if (((File)entry.getValue()).exists()){
                        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), ((File)entry.getValue()));
                        builder.addFormDataPart(entry.getKey(), ((File)entry.getValue()).getName(), imageBody);
                    }
                }else {
                    builder.addFormDataPart(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        List<MultipartBody.Part> parts = builder.build().parts();
        return  parts;
    }
    public static <K> K creatUploadService(Class<K> cls,UploadListener listener) {

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
        if (null!=listener){
            okhttpBuilder.addInterceptor(new UpLoadProgressInterceptor(listener));
        }

//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//        MultipartBody.Part no = MultipartBody.Part.createFormData("name", "myName");


//        Map<String, RequestBody> map = new HashMap<>();
//        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//        map.put("file\"; filename=\"" + file.getName() + " ", body);
//        map.put("nickname",RequestBody.create(null,nickname));

        OkHttpClient client = okhttpBuilder
                .retryOnConnectionFailure(true)
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(baseUrl)
                .build()
                .create(cls);
    }
    /**
     * 下载文件
     *
     * @param fileUrl
     * @return
     */
    public static Observable<ResponseBody> downloadFile(String fileUrl) {
        return DownloadRetrofit.downloadFile(fileUrl);
    }
}