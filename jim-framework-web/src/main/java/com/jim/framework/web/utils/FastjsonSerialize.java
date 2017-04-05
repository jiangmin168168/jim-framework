package com.jim.framework.web.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by jiang on 2017/3/29.
 */
public class FastjsonSerialize {

    private final static String DEFAULT_STRING="";

    public static <T> String serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("FastjsonSerialize.serialize: obj is null");
        }
        return JSON.toJSONString(obj);
    }

    public static <T> T deserialize(String jsonString,Class<T> targetClass) {

        if(StringUtils.isBlank(jsonString)){
            return null;
        }
        return JSON.parseObject(jsonString, targetClass);

    }
}
