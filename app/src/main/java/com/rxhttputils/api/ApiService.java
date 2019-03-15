package com.rxhttputils.api;


import com.rxhttputils.bean.User;
import com.rxjava.http.gsonconverter.DataNull;
import com.rxjava.http.transformer.Response;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * interface
 */

public interface ApiService {

    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <User> getUser();
    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <DataNull> getUser1();

    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <ResponseBody> getUser2();

    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <String> getUser3();

    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <Response<User>> getUserResponse();

    @GET("http://zzuli.gitee.io/api/userlist.html")
    Observable <List<User>> getUserList();

    @Multipart
    @POST
    Observable<ResponseBody> uploadFile(@Url String uploadUrl, @Part MultipartBody.Part file, @Part MultipartBody.Part  username );


    @Multipart
    @POST
    Observable<ResponseBody> uploadFile(@Url String uploadUrl,@Part List<MultipartBody.Part> partList);
}
