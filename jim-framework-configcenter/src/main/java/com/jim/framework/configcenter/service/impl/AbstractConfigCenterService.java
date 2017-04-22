package com.jim.framework.configcenter.service.impl;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractConfigCenterService {

    public Long getLongValue(String key){
        Object value=getObjectValue(key);
        if(value!=null){
            return Long.valueOf(String.valueOf(value));
        }
        return null;
    }
    public Integer getIntegerValue(String key){
        Object value=getObjectValue(key);
        if(value!=null){
            return Integer.valueOf(String.valueOf(value));
        }
        return null;
    }
    public Double getDoubleValue(String key){
        Object value=getObjectValue(key);
        if(value!=null){
            return Double.valueOf(String.valueOf(value));
        }
        return null;
    }
    public Boolean getBooleanValue(String key){
        Object value=getObjectValue(key);
        if(value!=null){
            return Boolean.parseBoolean(String.valueOf(value));
        }
        return null;
    }
    public String get(String key){
        Object value=getObjectValue(key);
        if(value!=null){
            return String.valueOf(value);
        }
        return null;
    }
    protected String checkKeyPrefix(String key){
        if(StringUtils.isEmpty(key)){
            return key;
        }
        if(key.startsWith(".")){
            return key;
        }
        return "."+key;
    }
    abstract public  Object getObjectValue(String key);

}
