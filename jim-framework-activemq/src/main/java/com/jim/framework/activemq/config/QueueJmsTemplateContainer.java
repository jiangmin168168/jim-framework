package com.jim.framework.activemq.config;

import org.springframework.jms.core.JmsTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangmin on 2018/1/27.
 */
public class QueueJmsTemplateContainer {

    private static volatile boolean needToRefresh;

    private static Map<String,JmsTemplate> queJmsTemplateMap=new ConcurrentHashMap<>();

    public static void setNeedToRefresh(boolean needToRefresh){
        QueueJmsTemplateContainer.needToRefresh=needToRefresh;
    }

    public static JmsTemplate getJmsTemplateByQueue(String queueName){
        while (QueueJmsTemplateContainer.needToRefresh){
            try {
                Thread.sleep(50);
                //System.out.println("peeding to refresh");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(queJmsTemplateMap.containsKey(queueName)){
            return queJmsTemplateMap.get(queueName);
        }
        return null;
    }

    public static void setQueJmsTemplateMap(String queueName,JmsTemplate jmsTemplate){
        setQueJmsTemplateMap(queueName,jmsTemplate,false);
    }

    public static void setQueJmsTemplateMap(String queueName,JmsTemplate jmsTemplate,boolean needToRefresh){
        QueueJmsTemplateContainer.needToRefresh=needToRefresh;
        if(!queJmsTemplateMap.containsKey(queueName)){
            queJmsTemplateMap.put(queueName,jmsTemplate);
            QueueJmsTemplateContainer.needToRefresh=false;
        }
        else {
            JmsTemplate jmsTemplateOld=queJmsTemplateMap.get(queueName);
            if(QueueJmsTemplateContainer.needToRefresh&& null!=jmsTemplateOld){
                //ConnectionFactoryContainer.stopProducerConnectionFactory();
                queJmsTemplateMap.clear();
                queJmsTemplateMap.put(queueName,jmsTemplate);
                QueueJmsTemplateContainer.needToRefresh=false;
            }
        }

    }
}
