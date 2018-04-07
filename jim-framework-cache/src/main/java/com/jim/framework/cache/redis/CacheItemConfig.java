package com.jim.framework.cache.redis;

import java.io.Serializable;

/**
 * Created by jiangmin on 2018/4/7.
 */
public class CacheItemConfig implements Serializable {

    private String name;
    private long expiryTimeSecond;
    private long preLoadTimeSecond;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpiryTimeSecond() {
        return expiryTimeSecond;
    }

    public void setExpiryTimeSecond(long expiryTimeSecond) {
        this.expiryTimeSecond = expiryTimeSecond;
    }

    public long getPreLoadTimeSecond() {
        return preLoadTimeSecond;
    }

    public void setPreLoadTimeSecond(long preLoadTimeSecond) {
        this.preLoadTimeSecond = preLoadTimeSecond;
    }
}
