package com.jim.framework.rpc.common;

/**
 * Created by jiang on 2017/5/14.
 */
public interface RpcInvoker {
    Object invoke(RpcInvocation invocation);
}
