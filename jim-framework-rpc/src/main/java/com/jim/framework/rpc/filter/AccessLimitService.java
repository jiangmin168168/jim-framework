package com.jim.framework.rpc.filter;

import com.jim.framework.rpc.common.RpcInvocation;

/**
 * 限流接口
 * 调用端自定义实现
 * Created by jiangmin on 2018/5/19.
 */
public interface AccessLimitService {

    void acquire(RpcInvocation invocation);
}
