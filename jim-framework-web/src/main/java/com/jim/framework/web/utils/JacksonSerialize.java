package com.jim.framework.web.utils;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonSerialize {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final static byte[] DEFAULT_BYTE=new byte[0];

    private final static Logger logger = LoggerFactory.getLogger(JacksonSerialize.class);

    static  {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static byte[] serialize(Object obj){
        if(null==obj){
            throw new RuntimeException("FastjsonSerialize.serialize: obj is null");
        }
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            logger.error("JacksonSerialize error:",e);
        }
        return DEFAULT_BYTE;
    }

    public static <T> T deserialize(byte[] bytes, Class<T> beanClass) {
        if (null==bytes||bytes.length==0){
            return null;
        }
        try {
            return objectMapper.readValue(bytes, beanClass);
        } catch (Exception e) {
            logger.error("JacksonSerialize error:",e);
        }
          return null;
    }
}
