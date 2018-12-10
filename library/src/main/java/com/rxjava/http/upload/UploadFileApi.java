package com.rxjava.http.upload;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 文件上传
 */

public interface UploadFileApi {

    @Multipart
    @POST
    Observable<ResponseBody> uploadImg(@Url String uploadUrl, @Part MultipartBody.Part file,@Part MultipartBody.Part  username );


    @Multipart
    @POST
    Observable<ResponseBody> uploadImg(@Url String uploadUrl,@Part List<MultipartBody.Part> partList);
}
