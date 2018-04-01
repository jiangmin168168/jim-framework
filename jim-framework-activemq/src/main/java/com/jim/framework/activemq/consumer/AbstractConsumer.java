package com.jim.framework.activemq.consumer;

import com.google.common.base.Strings;
import com.jim.framework.activemq.config.ConnectionFactoryContainer;
import com.jim.framework.activemq.config.ConsumerConfig;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangmin on 2018/1/10.
 */
public abstract class AbstractConsumer implements SessionAwareMessageListener,InitializingBean {


    private static Logger log = LoggerFactory.getLogger(AbstractConsumer.class);
    private Map<ConnectionFactory, DefaultMessageListenerContainer> listenerContainerMap = new ConcurrentHashMap();

    private ConsumerConfig consumerConfig;

    public AbstractConsumer(ConsumerConfig consumerConfig) {
        this.consumerConfig=consumerConfig;
    }

    public void afterPropertiesSet() {
        if(this.consumerConfig.getBrokerUrlList() != null && !this.consumerConfig.getBrokerUrlList().isEmpty() && !Strings.isNullOrEmpty(this.consumerConfig.getQueueName())) {

            for(String brokerUrl : this.consumerConfig.getBrokerUrlList()) {
                //ConnectionFactory connectionFactory = ConnectionFactoryContainer.getSingleConsumerConnectionFactory(brokerUrl);
                ConnectionFactory connectionFactory = ConnectionFactoryContainer.getConsumerConnectionFactory(brokerUrl);
                if (this.listenerContainerMap.containsKey(connectionFactory)) {
                    continue;
                }

                DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();

                defaultMessageListenerContainer.setCacheLevel(this.consumerConfig.getCacheLevel());
                defaultMessageListenerContainer.setConcurrentConsumers(this.consumerConfig.getConcurrentConsumers());
                ActiveMQQueue activeMQQueue = new ActiveMQQueue();
                activeMQQueue.setPhysicalName(this.consumerConfig.getQueueName());
                if(this.consumerConfig.getAcknowledgemode() < 0 || this.consumerConfig.getAcknowledgemode() > 4) {
                    this.consumerConfig.setAcknowledgemode( Session.AUTO_ACKNOWLEDGE);
                }

                if(this.consumerConfig.getAcknowledgemode() == Session.SESSION_TRANSACTED) {
                    defaultMessageListenerContainer.setSessionTransacted(true);
                }

                defaultMessageListenerContainer.setSessionAcknowledgeMode(this.consumerConfig.getAcknowledgemode());

                defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
                defaultMessageListenerContainer.setDestination(activeMQQueue);
                defaultMessageListenerContainer.setMessageListener(this);
                defaultMessageListenerContainer.initialize();
                defaultMessageListenerContainer.start();
                this.listenerContainerMap.put(connectionFactory, defaultMessageListenerContainer);

            }
        }
    }

    public void onMessage(Message message, Session session) throws JMSException {
        try {
            if(message == null) {
                log.info("received null data from {}", this.consumerConfig.getQueueName());
                return;
            }

            if(message instanceof ActiveMQObjectMessage) {
                Object object = ((ActiveMQObjectMessage)message).getObject();
                this.execute(object);
                this.commit(session);
            }
        } catch (Exception ex) {
            this.rollback(session);
            log.error("execute {} is failed:", this.consumerConfig.getQueueName(), ex);
            throw new JMSException(ex.getMessage());
        }

    }

    public abstract void execute(Object message) throws Exception;

    private void commit(Session session) throws JMSException {
        if(session.getTransacted()) {
            session.commit();
        }
    }

    private void rollback(Session session) throws JMSException {
        if(session.getTransacted()) {
            session.rollback();
        }
    }

}
