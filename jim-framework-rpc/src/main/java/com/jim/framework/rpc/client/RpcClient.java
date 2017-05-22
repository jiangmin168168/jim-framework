package com.jim.framework.rpc.client;

import com.jim.framework.rpc.common.RpcURL;
import com.jim.framework.rpc.config.ReferenceConfig;
import com.jim.framework.rpc.loadbalance.LoadbalanceService;
import com.jim.framework.rpc.loadbalance.RoundRobinLoadbalanceService;
import com.jim.framework.rpc.proxy.RpcProxy;
import com.jim.framework.rpc.registry.ConsulDiscoveryService;
import com.jim.framework.rpc.registry.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;

/**
RPC 客户端
 目前只是简单的获取远程接口代理
 后续扩展更多功能 TODO
 * Created by jiang on 2017/5/10.
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ReferenceConfig referenceConfig;

    private static LoadbalanceService loadbalanceService=new RoundRobinLoadbalanceService();

    public RpcClient(ReferenceConfig referenceConfig) {
       this.referenceConfig=referenceConfig;
        DiscoveryService discoveryService=new ConsulDiscoveryService();
        List<RpcURL> urls=discoveryService.getUrls(referenceConfig.getRegistryHost(),referenceConfig.getRegistryPort());
        int size=urls.size();
        int index = loadbalanceService.index(size);
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

