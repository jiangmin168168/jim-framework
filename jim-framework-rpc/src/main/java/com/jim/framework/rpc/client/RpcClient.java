package com.jim.framework.rpc.client;

import com.jim.framework.rpc.config.ReferenceConfig;
import com.jim.framework.rpc.proxy.RpcProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
/**
RPC 客户端
 目前只是简单的获取远程接口代理
 后续扩展更多功能 TODO
 * Created by jiang on 2017/5/10.
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ReferenceConfig referenceConfig;

    public RpcClient(ReferenceConfig referenceConfig) {
       this.referenceConfig=referenceConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy<T>(interfaceClass,this.referenceConfig)
        );
    }
}

