package com.jim.framework.configcenter.service.impl;

import com.google.common.base.Preconditions;
import com.jim.framework.configcenter.event.DataChangeEvent;
import com.jim.framework.configcenter.model.ConfigOption;
import com.jim.framework.configcenter.model.DefaultOptions;
import com.jim.framework.configcenter.service.ConfigCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigCenterServiceImpl implements ConfigCenterService {

    private final static Logger logger = LoggerFactory.getLogger(ConfigCenterServiceImpl.class);
    private static final Object initLockObj = new Object();
    private static final int checkZkLiveInterval = DefaultOptions.CHECK_ZK_LIVE_INTERVAL_MINUTES;
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean netInitFlag = new AtomicBoolean(false);
    private final AtomicBoolean initFlag = new AtomicBoolean(false);
    private ConfigOption configOption;
    private ConfigCenterService netConfig;
    private Map config;

    public ConfigCenterServiceImpl(ConfigOption configeOption) {
        this.configOption = Preconditions.checkNotNull(configeOption);
        this.config = new ConcurrentHashMap();
        this.init();
    }

    public void init() {
        if (this.initFlag.get()) {
            return;
        }
        synchronized (initLockObj) {
            if (this.initFlag.get()) {
                return;
            }

            if (this.configOption.isUseRemote()) {
                try {
                    this.initNet();
                } catch (Exception e) {
                    this.checkZkLiveStart();
                    logger.error("zk init error", e);
                }
            }

            this.initFlag.getAndSet(true);
        }

    }

    private void initNet() {
        this.netConfig = new ZkConfigCenterServiceImpl(this.configOption, this.config);
        this.configOption.getEnventBus().register(netConfig);
        this.netInitFlag.getAndSet(true);
    }

    private ConfigCenterService getConfigCenterService() {
        if (this.configOption.isUseRemote() && this.netInitFlag.get()) {
            return this.netConfig;
        }
        throw new RuntimeException("can not get ConfigCenterService");
    }

    private void checkZkLiveStart() {
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  try {
                                                                      if (!ConfigCenterServiceImpl.this.netInitFlag.get()) {
                                                                          initNet();
                                                                      }
                                                                  } catch (Exception e) {
                                                                      logger.error("init zk error", e);
                                                                  }

                                                              }
                                                          },
                0,
                this.checkZkLiveInterval,
                TimeUnit.MINUTES);
    }

    @Override
    public Map<String, Object> getConfig() {
        return this.getConfigCenterService().getConfig();
    }

    @Override
    public String get(String key) {
        return this.getConfigCenterService().get(key);
    }

    @Override
    public Long getLongValue(String key) {
        return this.getConfigCenterService().getLongValue(key);
    }

    @Override
    public Integer getIntegerValue(String key) {
        return this.getConfigCenterService().getIntegerValue(key);
    }

    @Override
    public Double getDoubleValue(String key) {
        return this.getConfigCenterService().getDoubleValue(key);
    }

    @Override
    public Boolean getBooleanValue(String key) {
        return this.getConfigCenterService().getBooleanValue(key);
    }

    @Override
    public void notify(DataChangeEvent event) {
        this.configOption.getEnventBus().post(event);
    }


    @Override
    public void colse() {
        this.scheduledExecutorService.shutdownNow();

        if(null!=netConfig) {
            this.netConfig.colse();
        }
        logger.info("config center closed");
    }

}
