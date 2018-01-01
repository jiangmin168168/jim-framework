package com.jim.framework.activemq.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by jiangmin on 2017/12/31.
 */
@Component
public class ProductConsumer {

    @JmsListener(destination = "jim.queue.product",containerFactory = "jmsListenerContainerQueue")
    public void receiveQueue(String text) {
        System.out.println("Consumer,productId:"+text);
    }

}
