package com.jim.framework.rpc.common;

/**
 * Created by jiang on 2017/5/14.
 */
public interface RpcFilter<T> {
    <T> T invoke(RpcInvoker invoker, RpcInvocation invocation);
}
