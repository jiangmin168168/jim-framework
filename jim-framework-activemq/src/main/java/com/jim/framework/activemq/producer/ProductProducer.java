package com.jim.framework.activemq.producer;

import com.jim.framework.activemq.config.Constans;
import com.jim.framework.activemq.config.QueueJmsTemplateContainer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by jiangmin on 2018/1/1.
 */
@Service
public class ProductProducer implements ProductSendMessage {

    @Override
    public void sendMessage(Object message) {

        JmsTemplate jmsTemplate= QueueJmsTemplateContainer.getJmsTemplateByQueue(Constans.QUEUE_NAME);
        jmsTemplate.convertAndSend(Constans.QUEUE_NAME,message);
    }
}
