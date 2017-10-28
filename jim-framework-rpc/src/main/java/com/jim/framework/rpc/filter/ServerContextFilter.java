package com.jim.framework.rpc.filter;

import com.jim.framework.rpc.common.RpcFilter;
import com.jim.framework.rpc.common.RpcInvocation;
import com.jim.framework.rpc.common.RpcInvoker;
import com.jim.framework.rpc.config.ConstantConfig;
import com.jim.framework.rpc.context.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by jiang on 2017/5/14.
 */
@ActiveFilter(group = {ConstantConfig.PROVIDER},order = -1000)
public class ServerContextFilter implements RpcFilter {

    private final static Logger logger = LoggerFactory.getLogger(ServerContextFilter.class);

    @Override
    public Object invoke(RpcInvoker invoker, RpcInvocation invocation) {
        Map<String,Object> contextParameters=invocation.getContextParameters();
        RpcContext.getContext().setContextParameters(contextParameters);
        Object rpcResponse=invoker.invoke(invocation);
        logger.info("ServerContextFilter.invoke end");
        return rpcResponse;
    }
}
