package com.jim.framework.activemq.consumer;

import com.jim.framework.activemq.config.ConsumerConfig;
import com.jim.framework.activemq.model.Product;
import org.springframework.stereotype.Service;

/**
 * Created by jiangmin on 2018/1/11.
 */
@Service
public class ProductConsumerC extends AbstractConsumer {


    public ProductConsumerC(ConsumerConfig consumerConfig) {
        super(consumerConfig);
    }

    @Override
    public void execute(Object message) throws Exception {
        Product.consumerMessageResult.add(message+"");
        System.out.println("Consumer,productId:" + message);
    }
}
