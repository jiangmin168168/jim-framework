package com.jim.framework.rpc.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcInvocation;
import com.jim.framework.rpc.common.RpcInvoker;
import com.jim.framework.rpc.config.ConstantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiang on 2017/5/14.
 */
@ActiveFilter(group = {ConstantConfig.CONSUMER})
public class AccessLimitFilter implements RpcFilter {

    private final static Logger logger = LoggerFactory.getLogger(AccessLimitFilter.class);

    @Override
    public Object invoke(RpcInvoker invoker, RpcInvocation invocation) {
        logger.info("before acquire");
        AccessLimitManager.acquire();

        Object rpcResponse=invoker.invoke(invocation);
        logger.info("after acquire");
        return rpcResponse;
    }

    static class AccessLimitManager{
        private final static RateLimiter rateLimiter=RateLimiter.create(2);

        public static void acquire(){
            rateLimiter.acquire();
        }
    }
}
