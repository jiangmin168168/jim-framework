package com.jim.framework.activemq.producer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.ConnectionKey;
import org.apache.activemq.jms.pool.ConnectionPool;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

/**
 * Created by jiangmin on 2018/1/21.
 */
public class JimPooledConnectionFactory extends PooledConnectionFactory {

    private GenericKeyedObjectPool<ConnectionKey, ConnectionPool> jimConnectionsPool;

    public GenericKeyedObjectPool<ConnectionKey, ConnectionPool> getJimConnectionsPool() {
        return jimConnectionsPool;
    }

    public JimPooledConnectionFactory() {
    }

    public JimPooledConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        super(activeMQConnectionFactory);
        this.jimConnectionsPool= this.getConnectionsPool();
    }

}
