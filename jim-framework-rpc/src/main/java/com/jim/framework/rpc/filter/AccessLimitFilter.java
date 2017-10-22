package com.jim.framework.rpc.filter;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcInvocation;
import com.jim.framework.rpc.common.RpcInvoker;
import com.jim.framework.rpc.config.ConstantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Created by jiang on 2017/5/14.
 */
@ActiveFilter(group = {ConstantConfig.CONSUMER})
public class AccessLimitFilter implements RpcFilter {

    private final static Logger logger = LoggerFactory.getLogger(AccessLimitFilter.class);

    @Override
    public Object invoke(RpcInvoker invoker, RpcInvocation invocation) {
        logger.info("before acquire,"+new Date());
        AccessLimitManager.acquire(invocation);

        Object rpcResponse=invoker.invoke(invocation);
        logger.info("after acquire,"+new Date());
        return rpcResponse;
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
