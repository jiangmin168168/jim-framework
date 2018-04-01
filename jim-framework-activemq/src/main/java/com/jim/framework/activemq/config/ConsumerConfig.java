package com.jim.framework.activemq.config;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.Session;
import java.util.List;

/**
 * Created by jiangmin on 2018/1/11.
 */
public class ConsumerConfig {

    private int concurrentConsumers = 10;
    private final int cacheLevel = DefaultMessageListenerContainer.CACHE_CONNECTION;
    private boolean useAsyncSend = true;
    private List<String> brokerUrlList;
    private int acknowledgemode = Session.AUTO_ACKNOWLEDGE;
    private String queueName;

    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public int getCacheLevel() {
        return cacheLevel;
    }

    public boolean isUseAsyncSend() {
        return useAsyncSend;
    }

    public void setUseAsyncSend(boolean useAsyncSend) {
        this.useAsyncSend = useAsyncSend;
    }

    public List<String> getBrokerUrlList() {
        return brokerUrlList;
    }

    public void setBrokerUrlList(List<String> brokerUrlList) {
        this.brokerUrlList = brokerUrlList;
    }

    public int getAcknowledgemode() {
        return acknowledgemode;
    }

    public void setAcknowledgemode(int acknowledgemode) {
        this.acknowledgemode = acknowledgemode;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
