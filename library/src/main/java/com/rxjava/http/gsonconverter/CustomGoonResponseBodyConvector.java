package com.rxjava.http.gsonconverter;

import com.rxjava.http.exception.ErrorType;
import com.rxjava.http.exception.ServerException;
import com.google.gson.TypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 统一处理服务器的返回值 针对结果统一进行成功或者失败的处理
 */

public class CustomGoonResponseBodyConvector<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;

    CustomGoonResponseBodyConvector(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert( ResponseBody value) throws IOException {
        try {
            String result = value.string();
            JSONObject response = new JSONObject(result);
            int code = response.optInt("code");
            if (ErrorType.SUCCESS==code) {
               // return adapter.fromJson(result);
                return adapter.fromJson(response.getString("data"));//解析data数据
            }else {
                String message = response.optString("msg", "");
                throw new ServerException(code, message);
            }

        } catch (JSONException e) {
            throw new ServerException(-1, "JSON解析异常");

        }
    }


}
