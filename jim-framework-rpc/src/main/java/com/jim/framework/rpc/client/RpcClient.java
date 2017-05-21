package com.jim.framework.rpc.client;

import com.jim.framework.rpc.common.RpcURL;
import com.jim.framework.rpc.config.ReferenceConfig;
import com.jim.framework.rpc.proxy.RpcProxy;
import com.jim.framework.rpc.registry.ConsulDiscoveryService;
import com.jim.framework.rpc.registry.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
RPC 客户端
 目前只是简单的获取远程接口代理
 后续扩展更多功能 TODO
 * Created by jiang on 2017/5/10.
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ReferenceConfig referenceConfig;

    private AtomicInteger roundRobin = new AtomicInteger(0);
    private static final int MAX_VALUE=1000;
    private static final int MIN_VALUE=1;

    private AtomicInteger getRoundRobinValue(){
        if(this.roundRobin.getAndAdd(1)>MAX_VALUE){
            this.roundRobin.set(MIN_VALUE);
        }
        return this.roundRobin;
    }

    public RpcClient(ReferenceConfig referenceConfig) {
       this.referenceConfig=referenceConfig;
        DiscoveryService discoveryService=new ConsulDiscoveryService();
        List<RpcURL> urls=discoveryService.getUrls(referenceConfig.getRegistryHost(),referenceConfig.getRegistryPort());
        int size=urls.size();
        int index = (this.getRoundRobinValue().get() + size) % size;
        logger.info("RpcClient init");
        RpcURL url= urls.get(index);
        this.referenceConfig.setHost(url.getHost());
        this.referenceConfig.setPort(url.getPort());

    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceClass,boolean isSync) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy<T>(interfaceClass,this.referenceConfig,isSync)
        );
    }
}

