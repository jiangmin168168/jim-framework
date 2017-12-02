package com.jim.framework.rpc.proxy;

import com.jim.framework.rpc.client.RpcReference;
import com.jim.framework.rpc.config.ReferenceConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RpcProxy <T> implements InvocationHandler {

    private Class<T> clazz;

    private boolean isSync=true;

    private ReferenceConfig referenceConfig;

    private RpcReference reference;

    public RpcProxy(Class<T> clazz,ReferenceConfig referenceConfig,RpcReference reference) {
        this.clazz = clazz;
        this.referenceConfig=referenceConfig;
        this.reference=reference;
        this.isSync=reference.isSync();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcHystrixCommand rpcHystrixCommand=new RpcHystrixCommand(proxy,method,args,this.reference,this.referenceConfig);
        return rpcHystrixCommand.execute();


    }
}
