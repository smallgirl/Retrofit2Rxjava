package com.rxhttputils.api;


import com.rxhttputils.bean.Ads;
import com.rxjava.http.transformer.Response;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * interface
 */

public interface ApiService {

    @Headers("REQUESTTYPE:UR_AdvertLogon")
    @GET("http://service.jd100.com/cgi-bin/phone/")
    Observable <Response<Ads>> getAds();

    @Headers("REQUESTTYPE:UR_AdvertLogon")
    @GET("http://service.jd100.com/cgi-bin/phone/")
    Observable <Ads> getAds1();

}
