package com.jim.framework.rpc.filter;

import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcInvocation;
import com.jim.framework.rpc.common.RpcInvoker;
import com.jim.framework.rpc.config.ConstantConfig;
import com.jim.framework.rpc.context.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiang on 2017/5/14.
 */
@ActiveFilter(group = {ConstantConfig.CONSUMER})
public class FutureFilter implements RpcFilter {

    private final static Logger logger = LoggerFactory.getLogger(FutureFilter.class);

    @Override
    public Object invoke(RpcInvoker invoker, RpcInvocation invocation) {
        Object rpcResponse=invoker.invoke(invocation);
        logger.info("clear future");
        RpcContext.removeContext();
        return rpcResponse;
    }
}
