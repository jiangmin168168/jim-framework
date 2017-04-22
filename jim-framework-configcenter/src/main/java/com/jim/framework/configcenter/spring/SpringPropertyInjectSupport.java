package com.jim.framework.configcenter.spring;

import com.jim.framework.configcenter.factory.ConfigCenterFactory;
import com.jim.framework.configcenter.service.ConfigCenterService;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class SpringPropertyInjectSupport {

    private String configNameSpaces = null;

    public String getConfigNameSpaces() {
        return configNameSpaces;
    }

    public void setConfigNameSpaces(String configNameSpaces) {
        this.configNameSpaces = configNameSpaces;
    }

    private void setSystemPropertiesFromConfigCenter() {
        if (StringUtils.isBlank(this.configNameSpaces)) {
            return;
        }
        ConfigCenterFactory.getInstance().setSystemNameSpace(this.configNameSpaces);
        ConfigCenterService cc = ConfigCenterFactory.getInstance().getConfig(this.configNameSpaces);
        Map<String, Object> config = cc.getConfig();
        setSystemProperys(cc, config);

    }

    private void setSystemProperys(ConfigCenterService cc, Map<String, Object> config) {
        for (String key : config.keySet()) {
            String value = cc.get(key);
            if (key.contains(".")) {
                key = key.substring(1);
            }
            if (value == null) {
                value = "";
            }
            System.setProperty(key, value);
        }
    }

    public void init() {
        if (this.configNameSpaces != null) {
            this.setSystemPropertiesFromConfigCenter();
        }
    }




}
