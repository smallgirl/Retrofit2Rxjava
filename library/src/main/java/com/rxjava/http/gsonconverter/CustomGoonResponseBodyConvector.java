package com.rxjava.http.gsonconverter;

import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 统一处理服务器的返回值 针对结果统一进行成功或者失败的处理
 * 可处理 解析data 不解析data data is void
 */

public class CustomGoonResponseBodyConvector<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;
    private boolean fromData;
    private Type type;

    CustomGoonResponseBodyConvector(TypeAdapter<T> adapter, Type type, boolean fromData) {
        this.adapter = adapter;
        this.type = type;
        this.fromData = fromData;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {

        return adapter.fromJson(ConvertUtil.convert(value.string(), fromData, type));
    }


}
