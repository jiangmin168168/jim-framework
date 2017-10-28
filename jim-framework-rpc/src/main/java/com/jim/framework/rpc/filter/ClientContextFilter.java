package com.jim.framework.rpc.filter;

import com.google.common.collect.Maps;
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
@ActiveFilter(group = {ConstantConfig.CONSUMER},order = -1000)
public class ClientContextFilter implements RpcFilter {

    private final static Logger logger = LoggerFactory.getLogger(ClientContextFilter.class);

    @Override
    public Object invoke(RpcInvoker invoker, RpcInvocation invocation) {
        Map<String,Object> contextParameters=invocation.getContextParameters();
        if(null==contextParameters){
            contextParameters= Maps.newHashMap();
        }
        Map<String,Object> contextParametersFromRpcContext= RpcContext.getContext().getContextParameters();
        if(null!=contextParametersFromRpcContext) {
            contextParameters.putAll(contextParametersFromRpcContext);
        }
        Object rpcResponse=invoker.invoke(invocation);
        logger.info("ClientContextFilter.invoke end");
        return rpcResponse;
    }
}
