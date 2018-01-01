package com.jim.framework.activemq.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Queue;

/**
 * Created by jiangmin on 2018/1/1.
 */
@Service
public class ProductProducer implements ProductSendMessage {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue productActiveMQQueue;


    @Override
    public void sendMessage(Object message) {

        this.jmsMessagingTemplate.convertAndSend(this.productActiveMQQueue,message);
    }
}
