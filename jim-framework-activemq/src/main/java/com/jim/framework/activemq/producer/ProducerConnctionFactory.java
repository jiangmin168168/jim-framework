package com.jim.framework.activemq.producer;

import com.jim.framework.activemq.config.ConnectionFactoryContainer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.transport.TransportListener;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jiangmin on 2018/1/21.
 */
public class ProducerConnctionFactory implements TransportListener {

    private static final Object lock=new Object();

    public PooledConnectionFactory create(String brokerClusterUrl){
        ActiveMQConnectionFactory mqConnectionFactory = new ActiveMQConnectionFactory();
        mqConnectionFactory.setBrokerURL(brokerClusterUrl);
        mqConnectionFactory.setTransportListener(this);
        //mqConnectionFactory.

        PooledConnectionFactory connectionFactory = new JimPooledConnectionFactory(mqConnectionFactory);
        connectionFactory.setMaxConnections(10);
        connectionFactory.setTimeBetweenExpirationCheckMillis(1000);
        //connectionFactory.setCreateConnectionOnStartup(true);

        return connectionFactory;
    }

    @Override
    public void onCommand(Object o) {
        if(o instanceof BrokerInfo){
            BrokerInfo brokerInfo=(BrokerInfo)o;
            System.out.println("onCommand"+brokerInfo.getBrokerURL());
            ConnectionFactoryContainer.addBrokerConnection(brokerInfo.getBrokerURL(),brokerInfo.getBrokerId()+"");
            synchronized (lock) {
                if (ProducerConnectionFactoryMonitor.connectionCount.containsKey(brokerInfo.getBrokerURL())) {
                    ProducerConnectionFactoryMonitor.connectionCount.get(brokerInfo.getBrokerURL()).incrementAndGet();
                } else {
                    ProducerConnectionFactoryMonitor.connectionCount.put(brokerInfo.getBrokerURL(), new AtomicLong(1));
                }
            }
            ProducerConnectionFactoryMonitor.currentConnectionsCount.incrementAndGet();
        }

    }

    @Override
    public void onException(IOException e) {
        System.out.println("onException");
    }

    @Override
    public void transportInterupted() {
        System.out.println("transportInterupted");
    }

    @Override
    public void transportResumed() {
        System.out.println("transportResumed");
    }
}
