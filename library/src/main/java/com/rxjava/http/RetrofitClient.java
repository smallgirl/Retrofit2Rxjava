package com.rxjava.http;


import android.util.Log;

import com.rxjava.http.download.DownloadRetrofit;
import com.rxjava.http.gsonconverter.CustomGoonConvertFactory;
import com.rxjava.http.transformer.Transformer;
import com.rxjava.http.upload.UpLoadProgressInterceptor;
import com.rxjava.http.upload.UploadFileApi;
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

import static com.rxjava.http.RetofitConfig.BASE_URL;
import static com.rxjava.http.RetofitConfig.DEFAULT_TIMEOUT;
import static com.rxjava.http.RetofitConfig.READ_TIMEOUT;


public class RetrofitClient {

    private static Retrofit retrofit;
    private static RetrofitClient instance;

    /**
     * 通用的全局请求
     * @param cls
     * @param <K>
     * @return
     */
    public static <K> K getApiService(Class<K> cls)  {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient(true))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    //.addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(CustomGoonConvertFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit.create(cls);
    }
    public static RetofitConfig newRetofit() {
        return new RetofitConfig();
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

    public static Observable<ResponseBody> uploadImg(String uploadUrl, String filePath, Map<String, String> mapParam) {
        return uploadImg(uploadUrl, filePath, mapParam, null);
    }
    // https://blog.csdn.net/huweijian5/article/details/52610093
    // https://blog.csdn.net/u012391876/article/details/52606817
    public static Observable<ResponseBody> uploadImg(String uploadUrl, String filePath, Map<String,String> mapParam,UploadListener listener) {

        File file = new File(filePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        MultipartBody.Part no = MultipartBody.Part.createFormData("name", "myName");


//        Map<String, RequestBody> map = new HashMap<>();
//        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//        map.put("file\"; filename=\"" + file.getName() + " ", body);
//        map.put("nickname",RequestBody.create(null,nickname));


        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (null!=mapParam && mapParam.size()>0){
            for (Map.Entry<String,String> entry : mapParam.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("file", file.getName(), imageBody);
        List<MultipartBody.Part> parts = builder.build().parts();

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        boolean isShowLog= true;
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
        if (null!=listener){
            okhttpBuilder.addInterceptor(new UpLoadProgressInterceptor(listener));
        }

        OkHttpClient client = okhttpBuilder
                .retryOnConnectionFailure(true)
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build()
                .create(UploadFileApi.class)
               // .uploadImg(uploadUrl, body,no)
                .uploadImg(uploadUrl, parts)
                .compose(Transformer.<ResponseBody>switchSchedulers());
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