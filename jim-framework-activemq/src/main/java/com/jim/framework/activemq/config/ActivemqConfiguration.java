package com.jim.framework.activemq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Queue;

/**
 * Created by jiangmin on 2017/12/31.
 */
@ComponentScan(basePackages = {"com.jim.framework.activemq"})
@Configuration
public class ActivemqConfiguration {

    private static final String BROKER_URL="failover:(tcp://192.168.10.222:61616,tcp://192.168.10.222:61617,tcp://192.168.10.222:61618,tcp://192.168.10.222:61619)";

    @Bean
    public Queue productActiveMQQueue(){
        return new ActiveMQQueue("jim.queue.product");
    }


    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue() {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(new ActiveMQConnectionFactory(BROKER_URL));
        return bean;
    }

    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate(){
        return new JmsMessagingTemplate(new ActiveMQConnectionFactory(BROKER_URL));
    }
}
