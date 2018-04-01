package com.jim.framework.activemq.producer;

import com.jim.framework.activemq.config.ConnectionFactoryContainer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.transport.TransportListener;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jiangmin on 2018/1/21.
 */
public class ConsumerConnctionFactory implements TransportListener {

    private static final Object lock=new Object();

    private static final Map<String,AtomicLong> connectionCount=new ConcurrentHashMap<>();


    public PooledConnectionFactory create(String brokerClusterUrl){
        ActiveMQConnectionFactory mqConnectionFactory = new ActiveMQConnectionFactory();
        mqConnectionFactory.setBrokerURL(brokerClusterUrl);
        mqConnectionFactory.setTransportListener(this);
        //mqConnectionFactory.

        PooledConnectionFactory connectionFactory = new JimPooledConnectionFactory(mqConnectionFactory);
        connectionFactory.setMaxConnections(1);
        connectionFactory.setCreateConnectionOnStartup(true);

        return connectionFactory;
    }
    @Override
    public void onCommand(Object o) {
        if(o instanceof BrokerInfo){
            BrokerInfo brokerInfo=(BrokerInfo)o;
            System.out.println("onCommand"+brokerInfo.getBrokerURL());
            ConnectionFactoryContainer.addBrokerConnection(brokerInfo.getBrokerURL(),brokerInfo.getBrokerId()+"");
            if (connectionCount.containsKey(brokerInfo.getBrokerURL())) {
                connectionCount.get(brokerInfo.getBrokerURL()).incrementAndGet();
            }
            else {
                connectionCount.put(brokerInfo.getBrokerURL(),new AtomicLong(1));
            }
        }
        else if(o instanceof WireFormatInfo){
            WireFormatInfo wireFormatInfo=(WireFormatInfo)o;
            System.out.println("onCommand WireFormatInfo");
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
