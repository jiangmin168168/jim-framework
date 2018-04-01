package com.jim.framework.activemq.config;

import com.jim.framework.activemq.consumer.ProductConsumerC;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.List;

/**
 * Created by jiangmin on 2017/12/31.
 */
@ComponentScan(basePackages = {"com.jim.framework.activemq"})
@Configuration
public class ActivemqConfiguration {

    @Autowired
    private PooledConnectionFactory pooledConnectionFactory;

    @Bean
    public Queue productActiveMQQueue(){
        return new ActiveMQQueue(Constans.QUEUE_NAME);
    }


    //@Bean
    public JmsListenerContainerFactory<?> jmsListenerAContainerQueue() {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();

        bean.setConnectionFactory(this.pooledConnectionFactory);
        bean.setConcurrency("3-10");

        return bean;
    }

    //@Bean
    public JmsListenerContainerFactory<?> jmsListenerBContainerQueue() {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory(Constans.CONSUMER_B_BROKER_URL);
        //connectionFactory
        bean.setConnectionFactory(connectionFactory);
        bean.setConcurrency("3-10");

        return bean;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory(){
//        ActiveMQConnectionFactory activeMQConnectionFactory=new ActiveMQConnectionFactory(Constans.PRODUCER_BROKER_URL);
//        //activeMQConnectionFactory.
//        activeMQConnectionFactory.setPrefetchPolicy(new ActiveMQPrefetchPolicy());
//        PooledConnectionFactory pooledConnectionFactory=new PooledConnectionFactory(activeMQConnectionFactory);
//        pooledConnectionFactory.setMaxConnections(10);
//
//        pooledConnectionFactory.setMaximumActiveSessionPerConnection(50);
////        pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(3000);
////        pooledConnectionFactory.setIdleTimeout(10000);
//        //pooledConnectionFactory.setCreateConnectionOnStartup(true);
//        return pooledConnectionFactory;
        String brokerClusterString=ConnectionFactoryContainer.buildProducerBrokerClusterUri(Constans.PRODUCER_BROKER_URL);
        return ConnectionFactoryContainer.getPooledConnectionFactory(brokerClusterString);
    }

    //@Bean
    public JmsMessagingTemplate jmsMessagingTemplate(){

        JmsMessagingTemplate jmsMessagingTemplate= new JmsMessagingTemplate(this.pooledConnectionFactory);

        return jmsMessagingTemplate;
    }

    @PostConstruct
    public void init(){
        String brokerClusterString=ConnectionFactoryContainer.buildProducerBrokerClusterUri(Constans.PRODUCER_BROKER_URL);
        PooledConnectionFactory pooledConnectionFactory= ConnectionFactoryContainer.getPooledConnectionFactory(brokerClusterString);
        JmsTemplate jmsTemplate=new JmsTemplate(pooledConnectionFactory);
        QueueJmsTemplateContainer.setQueJmsTemplateMap(Constans.QUEUE_NAME,jmsTemplate);

    }

    @Bean
    public ProductConsumerC productConsumerC(){
        ConsumerConfig consumerConfig=new ConsumerConfig();

        List<String> brokerUris=ConnectionFactoryContainer.buildConsumerBrokerClusterUri(Constans.PRODUCER_BROKER_URL);

        consumerConfig.setBrokerUrlList(brokerUris);
        consumerConfig.setQueueName(Constans.QUEUE_NAME);
        //consumerConfig.setQueueName(Constans.QUEUE_NAME+"?consumer.prefetchSize=5");
        consumerConfig.setAcknowledgemode(Session.CLIENT_ACKNOWLEDGE);
        return new ProductConsumerC(consumerConfig);
    }
}
