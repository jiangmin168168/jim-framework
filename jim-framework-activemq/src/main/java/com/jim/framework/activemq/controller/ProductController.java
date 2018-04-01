package com.jim.framework.activemq.controller;

import com.google.common.base.Stopwatch;
import com.jim.framework.activemq.config.ConnectionFactoryContainer;
import com.jim.framework.activemq.model.Product;
import com.jim.framework.activemq.producer.JimPooledConnectionFactory;
import com.jim.framework.activemq.producer.ProductProducer;
import org.apache.activemq.jms.pool.ConnectionKey;
import org.apache.activemq.jms.pool.ConnectionPool;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangmin on 2017/12/31.
 */


@RestController
@RequestMapping("/product")
public class ProductController{

    @Autowired
    private ProductProducer productProducer;

    @RequestMapping("/{productId}")
    public Long getById(@PathVariable final long productId) {

        int count=(int)productId;

        final ProductProducer productProducerLocal=this.productProducer;
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < count; i++) {
                        try {
                            Thread.sleep(20);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        productProducerLocal.sendMessage(i);
                    }

                }
            }
        });
        thread.start();

        return productId;
    }

    @RequestMapping("/batch/{count}")
    public Long batch(@PathVariable final long count) {
        final ProductProducer productProducerLocal=this.productProducer;
        Product.consumerMessageResult.clear();
        Stopwatch stopwatch=Stopwatch.createStarted();
        ExecutorService executorService= Executors.newFixedThreadPool(10);
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(50);
                final int productId=i;

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        productProducerLocal.sendMessage(productId);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        stopwatch.stop();
        System.out.println("producer time:"+stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return count;
    }


    @RequestMapping("/test/{productId}")
    public Long test(@PathVariable final long productId) {

        productProducer.sendMessage(productId);

        Map<String,PooledConnectionFactory> pooledConnectionFactoryMap= ConnectionFactoryContainer.getAllPooledConnectionFactory();
        for(Map.Entry<String,PooledConnectionFactory> entry:pooledConnectionFactoryMap.entrySet()) {
            JimPooledConnectionFactory jimPooledConnectionFactory=(JimPooledConnectionFactory) entry.getValue();

            //jimPooledConnectionFactory.setExpiryTimeout();
            GenericKeyedObjectPool<ConnectionKey, ConnectionPool> jimConnectionsPool = ((JimPooledConnectionFactory) entry.getValue()).getJimConnectionsPool();

            //jimConnectionsPool.
            jimConnectionsPool.clearOldest();
            //jimConnectionsPool.set
            Map<String, List<DefaultPooledObjectInfo>> defStringListMap= jimConnectionsPool.listAllObjects();
            for(Map.Entry<String,List<DefaultPooledObjectInfo>> entry1 : defStringListMap.entrySet()){
                List<DefaultPooledObjectInfo> defaultPooledObjectInfos=entry1.getValue();
                System.out.println("123");
                for(DefaultPooledObjectInfo defaultPooledObjectInfo:defaultPooledObjectInfos){
                    //defaultPooledObjectInfo.
                    System.out.println("123");
                    //((ConnectionPool)defaultPooledObjectInfo.pooledObject.getObject()).connection;
                }
            }
            //jimConnectionsPool.get
            System.out.println("123");

            //((ObjectDeque)((java.util.concurrent.ConcurrentHashMap.MapEntry)((java.util.concurrent.ConcurrentHashMap)jimConnectionsPool.poolMap).entrySet().toArray()[0]).getValue()).allObjects
        }

        return productId;
    }

    @RequestMapping("/result")
    public String getResult(){
        return Product.consumerMessageResult.size()+"";
    }


}
