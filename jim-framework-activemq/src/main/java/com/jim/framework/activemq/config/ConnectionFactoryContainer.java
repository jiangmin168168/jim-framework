package com.jim.framework.activemq.config;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jim.framework.activemq.producer.ConsumerConnctionFactory;
import com.jim.framework.activemq.producer.ProducerConnctionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jiangmin on 2018/1/10.
 */
public class ConnectionFactoryContainer {

    public static Map<String, PooledConnectionFactory> producerConnectionFactoryMap = new ConcurrentHashMap(2);

    public static Map<String, PooledConnectionFactory> consumerConnectionFactoryMap = new ConcurrentHashMap(2);

    private static List<PooledConnectionFactory> needToRemoveConnctionFactories=Lists.newArrayList();

    public static Map<String,Set<String>> brokerConnections=new ConcurrentHashMap<>();

    public static ExecutorService executorService= Executors.newSingleThreadExecutor();

    private static final Object lock=new Object();

    static {
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    if(!CollectionUtils.isEmpty(needToRemoveConnctionFactories)){
//                        for(PooledConnectionFactory pooledConnectionFactory:needToRemoveConnctionFactories){
//                            pooledConnectionFactory.stop();
//                            needToRemoveConnctionFactories.remove(pooledConnectionFactory);
//                        }
//                    }
//
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

    public static void stopProducerConnectionFactory(){
        for(Map.Entry<String,PooledConnectionFactory> entry: producerConnectionFactoryMap.entrySet()){
            PooledConnectionFactory pooledConnectionFactory=entry.getValue();
            if(null!=pooledConnectionFactory){
                //pooledConnectionFactory.stop();
                needToRemoveConnctionFactories.add(pooledConnectionFactory);
                producerConnectionFactoryMap.remove(entry.getKey());
            }
        }
    }

    public static void producerConnectionFactoryRebalance(){
        for(Map.Entry<String,PooledConnectionFactory> entry: producerConnectionFactoryMap.entrySet()){
            PooledConnectionFactory pooledConnectionFactory=entry.getValue();
            if(null!=pooledConnectionFactory){
                pooledConnectionFactory.setExpiryTimeout(30*1*1000);
                System.out.println("expirytimeount set to 60*2*1000");
            }
        }
    }

    public static void producerConnectionFactoryResetExpiryTimeout(){
        for(Map.Entry<String,PooledConnectionFactory> entry: producerConnectionFactoryMap.entrySet()){
            PooledConnectionFactory pooledConnectionFactory=entry.getValue();
            if(null!=pooledConnectionFactory){
                if(pooledConnectionFactory.getExpiryTimeout()!=0) {
                    pooledConnectionFactory.setExpiryTimeout(0);
                    System.out.println("expirytimeount set to 0");
                }
            }
        }
    }

    public static Map<String,PooledConnectionFactory> getAllPooledConnectionFactory(){
        return producerConnectionFactoryMap;
    }

    public static void addBrokerConnection(String brokerUri,String brokerConnetionId){
        synchronized (lock){
            if(brokerConnections.containsKey(brokerUri)){
                Set<String> connetions=brokerConnections.get(brokerUri);
                if(!CollectionUtils.isEmpty(connetions)){
                    connetions.add(brokerConnetionId);
                }
                else {
                    connetions= Sets.newConcurrentHashSet(Arrays.asList(brokerConnetionId));
                }
                brokerConnections.put(brokerUri,connetions);
            }
            else {
                brokerConnections.put(brokerUri,Sets.newConcurrentHashSet(Arrays.asList(brokerConnetionId)));
            }
        }
        StringBuilder stringBuilder=new StringBuilder();
        for(Map.Entry<String,Set<String>> entry:brokerConnections.entrySet()){
            stringBuilder.append("uri:"+entry.getKey());
            stringBuilder.append(Joiner.on(";").join(entry.getValue()));
        }
        System.out.println(stringBuilder.toString());
    }


    public static PooledConnectionFactory createPooledConnectionFactory(String brokerUrl) {

        final String brokerClusterUrl=brokerUrl.replace(";",",");
        PooledConnectionFactory connectionFactory = null;



        //((ActiveMQConnectionFactory)connectionFactory.getConnectionFactory()).get;
        synchronized(lock) {
            if(producerConnectionFactoryMap.containsKey(brokerClusterUrl)) {
                connectionFactory = producerConnectionFactoryMap.get(brokerClusterUrl);

                needToRemoveConnctionFactories.add(connectionFactory);
                producerConnectionFactoryMap.remove(brokerUrl);
            }
            ProducerConnctionFactory producerConnctionFactory=new ProducerConnctionFactory();
            //producerConnctionFactory.init();
            connectionFactory=producerConnctionFactory.create(brokerClusterUrl);


            producerConnectionFactoryMap.put(brokerClusterUrl, connectionFactory);

            return connectionFactory;
        }
    }

    public static PooledConnectionFactory getPooledConnectionFactory(String brokerUrl) {
        final String brokerClusterUrl=brokerUrl.replace(";",",");
        PooledConnectionFactory connectionFactory = null;



        //((ActiveMQConnectionFactory)connectionFactory.getConnectionFactory()).get;
        synchronized(lock) {
            if(producerConnectionFactoryMap.containsKey(brokerClusterUrl)) {
                connectionFactory = producerConnectionFactoryMap.get(brokerClusterUrl);
            } else {
                ProducerConnctionFactory producerConnctionFactory=new ProducerConnctionFactory();
                //producerConnctionFactory.init();
                connectionFactory=producerConnctionFactory.create(brokerClusterUrl);


                producerConnectionFactoryMap.put(brokerClusterUrl, connectionFactory);
            }

            return connectionFactory;
        }
    }


    public static PooledConnectionFactory getConsumerConnectionFactory(String brokerUrl) {
        PooledConnectionFactory connectionFactory = null;
        synchronized(lock) {
            if(consumerConnectionFactoryMap.containsKey(brokerUrl)) {
                connectionFactory = (PooledConnectionFactory)consumerConnectionFactoryMap.get(brokerUrl);
            } else {
                ConsumerConnctionFactory consumerConnctionFactory=new ConsumerConnctionFactory();
                //producerConnctionFactory.init();
                connectionFactory=consumerConnctionFactory.create(brokerUrl);
                consumerConnectionFactoryMap.put(brokerUrl, connectionFactory);
            }

            return connectionFactory;
        }
    }

    public static String buildProducerBrokerClusterUri(String brokerString){
        String prefixBrokerString="failover:";
        String brokerTempString=brokerString.substring(brokerString.indexOf("(")+1,brokerString.indexOf(")"));
        brokerTempString=brokerTempString.replace(";",",");
        String brokerParamString=brokerString.substring(brokerString.indexOf(")")+1);
        int virtualBrockerTimes=5;
        StringBuilder stringBuilder=new StringBuilder();

        stringBuilder.append(prefixBrokerString+"(");
        for(int i=0;i<virtualBrockerTimes;i++) {
            stringBuilder.append(brokerTempString);
            if(i!=virtualBrockerTimes-1){
                stringBuilder.append(",");
            }

        }
        stringBuilder.append(")"+brokerParamString);
        System.out.println("brockerClusterUri:"+stringBuilder.toString());
        return stringBuilder.toString();
    }

    public static List<String> buildConsumerBrokerClusterUri(String brokerString){
        String prefixBrokerString="failover:";
        String brokerTempString=brokerString.substring(brokerString.indexOf("(")+1,brokerString.indexOf(")"));
        String brokerParamString=brokerString.substring(brokerString.indexOf(")")+1);
        List<String> brokers= Lists.newArrayList(brokerTempString.split(";"));
        List<String> brokerUris=Lists.newArrayList();
        for(String broker:brokers){
            brokerUris.add(prefixBrokerString+"("+broker+")"+brokerParamString);
        }
        return brokerUris;
    }
}
