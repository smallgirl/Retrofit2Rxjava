package com.rxjava.http.gsonconverter;

import com.google.gson.TypeAdapter;
import com.rxjava.http.exception.ErrorType;
import com.rxjava.http.exception.ServerException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 统一处理服务器的返回值 针对结果统一进行成功或者失败的处理
 * 可处理 解析data 不解析data data is void
 *
 */

public class CustomGoonResponseBodyConvector<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;
    private boolean fromData;
    private Type type;

    CustomGoonResponseBodyConvector(TypeAdapter<T> adapter, Type type,boolean fromData) {
        this.adapter = adapter;
        this.type = type;
        this.fromData = fromData;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String result = value.string();
            JSONObject response = new JSONObject(result);
            int code = response.optInt("code");
            if (ErrorType.SUCCESS == code) {
                String data = fromData ? response.getString("data") : result;
                if (DataNull.class == type) {
                    return adapter.fromJson("{}");//解析data数据
                }
                return adapter.fromJson(data);//解析data数据
            } else {
                String message = response.optString("msg", "");
                throw new ServerException(code, message);
            }

        } catch (JSONException e) {
            throw new ServerException(ErrorType.PARSE_ERROR, "JSON解析异常");

        }
    }


}
