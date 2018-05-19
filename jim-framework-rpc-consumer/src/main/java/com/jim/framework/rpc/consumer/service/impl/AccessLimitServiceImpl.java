package com.jim.framework.rpc.consumer.service.impl;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.jim.framework.rpc.common.RpcInvocation;
import com.jim.framework.rpc.filter.AccessLimitService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by jiangmin on 2018/5/19.
 */
@Service
public class AccessLimitServiceImpl implements AccessLimitService {

    @Override
    public void acquire(RpcInvocation invocation) {
        AccessLimitManager.acquire(invocation);
    }


    static class AccessLimitManager{

        private final static Object lock=new Object();

        private final static Map<String,RateLimiter> rateLimiterMap= Maps.newHashMap();

        public static void acquire(RpcInvocation invocation){
            if(!rateLimiterMap.containsKey(invocation.getClassName())) {
                synchronized (lock) {
                    if(!rateLimiterMap.containsKey(invocation.getClassName())) {
                        final RateLimiter rateLimiter = RateLimiter.create(invocation.getMaxExecutesCount());
                        rateLimiterMap.put(invocation.getClassName(), rateLimiter);
                    }
                }
            }
            else {
                RateLimiter rateLimiter=rateLimiterMap.get(invocation.getClassName());
                rateLimiter.acquire();
            }
        }
    }
}
