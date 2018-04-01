package com.jim.framework.activemq.producer;

import com.jim.framework.activemq.config.ConnectionFactoryContainer;
import com.jim.framework.activemq.config.Constans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jiangmin on 2018/1/29.
 */
public class ProducerConnectionFactoryMonitor {

    public static final Map<String,AtomicLong> connectionCount=new ConcurrentHashMap<>();

    private static volatile long startTime=0;

    private static volatile long doBalanceStartTime=0;

    private static ExecutorService executorService= Executors.newFixedThreadPool(2);

    public static final AtomicInteger currentConnectionsCount=new AtomicInteger(0);

    static {
        startTime=System.currentTimeMillis();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                boolean isRunning=false;
                while (isRunning) {
                    long currentTime=System.currentTimeMillis();
                    long diffSecond=(currentTime-startTime)/1000;
                    if(diffSecond>=10&&currentConnectionsCount.get()>= Constans.MAX_CONNETIONS_COUNT_PRODUCER) {
                        StringBuilder stringBuilder=new StringBuilder();
                        stringBuilder.append("check result:connections count:"+currentConnectionsCount.get());
                        for (Map.Entry<String, AtomicLong> entry : connectionCount.entrySet()) {
                            stringBuilder.append("broker uri:" + entry.getKey() + ";count:" + entry.getValue());

                            currentConnectionsCount.set(0);
                            connectionCount.clear();
                            if (entry.getValue().get() < 4|| entry.getValue().get()>6) {
                                stringBuilder.append("broker need rebalance:" + entry.getKey());

                                ProducerConnectionFactoryMonitor.doBalanceStartTime=System.currentTimeMillis();
                                ConnectionFactoryContainer.producerConnectionFactoryRebalance();


                                break;
                            }

                        }

                        System.out.println(stringBuilder.toString());
                    }
                    else {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });



        executorService.submit(new Runnable() {
            @Override
            public void run() {
                boolean isRunning=false;
                while (isRunning) {
                    long currentTime=System.currentTimeMillis();
                    long diffSecond=(currentTime-ProducerConnectionFactoryMonitor.doBalanceStartTime)/1000;
                    if(diffSecond>=20&&ProducerConnectionFactoryMonitor.doBalanceStartTime>0) {
                        StringBuilder stringBuilder=new StringBuilder();
                        stringBuilder.append("check result:connections count:"+currentConnectionsCount.get());
                        ConnectionFactoryContainer.producerConnectionFactoryResetExpiryTimeout();
                        ProducerConnectionFactoryMonitor.doBalanceStartTime=0;
                        System.out.println(stringBuilder.toString());
                    }
                    else {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

}
