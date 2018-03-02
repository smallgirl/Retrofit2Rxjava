package com.rxhttputils.api;


import com.rxhttputils.bean.User;
import com.rxjava.http.transformer.Response;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * interface
 */

public interface ApiService {

    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <User> getUser();

    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <Response<User>> getUserResponse();

    @GET("http://zzuli.gitee.io/api/userlist.html")
    Observable <List<User>> getUserList();

}
