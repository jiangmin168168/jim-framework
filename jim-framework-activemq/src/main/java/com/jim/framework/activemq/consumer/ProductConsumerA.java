package com.jim.framework.activemq.consumer;

import com.jim.framework.activemq.model.Product;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangmin on 2017/12/31.
 */
@Component
public class ProductConsumerA implements MessageListener {

    //@JmsListener(destination = Constans.QUEUE_NAME,containerFactory = Constans.LISTENER_A_CONTAINER_QUEUE_NAME)
    @Override
    public void onMessage(Message message) {

        String transport= ((ActiveMQConnection)((ActiveMQObjectMessage)message).getConnection()).getTransport().toString();

        String text= "";
        try {
            text = ((ActiveMQObjectMessage)message).getObject().toString();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        if(Product.transportResult.containsKey(transport)){
            Product.transportResult.get(transport).add(text);
        }
        else {
            List<String> productList=new ArrayList<>();
            productList.add(text);
            Product.transportResult.put(transport,productList);
        }
        System.out.println("Consumer,productId:"+text+"transport:"+transport);
    }
}
