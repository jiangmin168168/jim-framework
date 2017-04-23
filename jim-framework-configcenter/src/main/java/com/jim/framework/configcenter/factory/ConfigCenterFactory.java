package com.jim.framework.configcenter.factory;

import com.google.common.base.Preconditions;
import com.jim.framework.configcenter.model.ConfigOption;
import com.jim.framework.configcenter.model.DefaultOptions;
import com.jim.framework.configcenter.service.ConfigCenterService;
import com.jim.framework.configcenter.service.impl.ConfigCenterServiceImpl;
import com.jim.framework.configcenter.utils.ZKUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigCenterFactory {

    private final static Logger logger = LoggerFactory.getLogger(ConfigCenterFactory.class);

    private static final Object lockObj = new Object();

    private ConcurrentHashMap<String, ConfigCenterService> configCenterCache = null;
    private String defaultZkurl;

    private String systemNameSpace;
    public String getSystemNameSpace() {
        return systemNameSpace;
    }

    public void setSystemNameSpace(String systemNameSpace) {
        this.systemNameSpace = systemNameSpace;
    }

    private ConfigCenterFactory() {
        this.configCenterCache = new ConcurrentHashMap<String, ConfigCenterService>();
    }

    private static class ConfigCenterFactoryHolder {
        public static ConfigCenterFactory configCenterFactory = new ConfigCenterFactory();
    }

    private String getDefaultZkurl() {
        if (StringUtils.isNotEmpty(this.defaultZkurl)) {
            return this.defaultZkurl;
        }
        this.defaultZkurl = ZKUtil.getZkurl();
        if (StringUtils.isEmpty(this.defaultZkurl)) {
            throw new RuntimeException("can not get zk url,you must config it, file name:" + DefaultOptions.CONFIG_NAME);
        }
        return this.defaultZkurl;
    }
    public static ConfigCenterFactory getInstance() {
        return ConfigCenterFactoryHolder.configCenterFactory;
    }

    public ConfigCenterService getConfig(final String hosts, final String nameSpace) {

        Preconditions.checkNotNull(hosts);
        Preconditions.checkNotNull(nameSpace);

        StringBuilder sb = new StringBuilder(hosts);
        sb.append(nameSpace);

        final String key = sb.toString().intern();

        ConfigCenterService config = this.configCenterCache.get(key);
        if (config == null) {
            synchronized (lockObj) {
                if (!this.configCenterCache.containsKey(key)) {
                    ConfigOption co = new ConfigOption(nameSpace, hosts);
                    ConfigCenterService cc = new ConfigCenterServiceImpl(co);
                    this.configCenterCache.put(key, cc);
                }
            }
        } else {
            return config;
        }

        return this.configCenterCache.get(key);
    }

    public ConfigCenterService getConfigCenterService(final String nameSpace) {
        return getConfig(getDefaultZkurl(), nameSpace);
    }

    public ConfigCenterService getConfig() {
        if (StringUtils.isBlank(this.systemNameSpace)) {
            throw new RuntimeException("config center namespace is null");
        }
        return getConfig(getDefaultZkurl(), getSystemNameSpace());
    }


}
