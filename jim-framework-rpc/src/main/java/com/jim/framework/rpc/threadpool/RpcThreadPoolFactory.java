package com.jim.framework.rpc.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/2/002.
 */
@Component
public class RpcThreadPoolFactory {

    @Autowired
    private Map<String,RpcThreadPool> rpcThreadPoolMap;

    public RpcThreadPool getThreadPool(String threadPoolName){
        return this.rpcThreadPoolMap.get(threadPoolName);
    }
}
