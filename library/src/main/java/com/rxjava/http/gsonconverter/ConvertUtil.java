package com.rxjava.http.gsonconverter;

import com.rxjava.http.exception.ErrorType;
import com.rxjava.http.exception.ServerException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class ConvertUtil {
    public static String convert(String result, boolean fromData, Type type) {
        try {
            JSONObject response = new JSONObject(result);
            int code = response.optInt("code");
            if (ErrorType.SUCCESS == code) {
                if (DataNull.class == type) {
                    return "{}";//rxjava 不能发射空数据
                }
                String data = fromData ? response.optString("data") : result;
                if (null == data || "null".equals(data)) {
                    if (type.toString().startsWith("java.util.List")) {
                        return "[]"; // rxjava 不能发送 空对象
                    } else {
                        return "{}"; // rxjava 不能发送 空对象
                    }
                }
                return data;
            } else {
                String message = response.optString("msg", "");
                throw new ServerException(code, message, result);
            }

        } catch (JSONException e) {
            throw new ServerException(ErrorType.PARSE_ERROR, "JSON解析异常", result);

        }

    }
}
