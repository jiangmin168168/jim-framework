package com.jim.framework.rpc.filter;

import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcInvocation;
import com.jim.framework.rpc.common.RpcInvoker;
import com.jim.framework.rpc.config.ConstantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiang on 2017/5/14.
 */
@ActiveFilter(group = {ConstantConfig.PROVIDER,ConstantConfig.CONSUMER})
public class AccessLogFilter implements RpcFilter {

    private final static Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public Object invoke(RpcInvoker invoker, RpcInvocation invocation) {
        logger.info("before call");
        Object rpcResponse=invoker.invoke(invocation);
        logger.info("after call");
        return rpcResponse;
    }
}
