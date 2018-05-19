package com.jim.framework.rpc.filter;

import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcInvocation;
import com.jim.framework.rpc.common.RpcInvoker;
import com.jim.framework.rpc.config.ConstantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by jiang on 2017/5/14.
 */
@ActiveFilter(group = {ConstantConfig.CONSUMER})
public class AccessLimitFilter implements RpcFilter {

    private final static Logger logger = LoggerFactory.getLogger(AccessLimitFilter.class);

    @Override
    public Object invoke(RpcInvoker invoker, RpcInvocation invocation) {
        logger.info("before acquire,"+new Date());
        List<AccessLimitService> accessLimitServiceLoader = SpringFactoriesLoader.loadFactories(AccessLimitService.class, null);
        if(!CollectionUtils.isEmpty(accessLimitServiceLoader)){
            AccessLimitService accessLimitService=accessLimitServiceLoader.get(0);
            accessLimitService.acquire(invocation);
        }

        Object rpcResponse=invoker.invoke(invocation);
        logger.info("after acquire,"+new Date());
        return rpcResponse;
    }
}
