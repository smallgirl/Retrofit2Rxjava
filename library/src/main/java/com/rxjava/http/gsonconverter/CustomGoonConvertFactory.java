package com.rxjava.http.gsonconverter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


public class CustomGoonConvertFactory extends Converter.Factory {

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static CustomGoonConvertFactory create(boolean fromData) {
        return create(new Gson(),fromData);
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static CustomGoonConvertFactory create(Gson gson,boolean fromData) {
        return new CustomGoonConvertFactory(gson,fromData);
    }

    private final Gson gson;
    private boolean fromData;


    private CustomGoonConvertFactory(Gson gson,boolean fromData) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        this.fromData=fromData;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<ResponseBody, String>() {
                @Override
                public String convert(ResponseBody value) throws IOException {
                    return value.string();
                }
            };
        }
        Log.e("tag",type.toString());
        final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CustomGoonResponseBodyConvector<>(adapter, type,fromData);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CustomGsonRequestBodyConverter<>(gson, adapter);
    }
}