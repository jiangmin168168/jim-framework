package com.jim.framework.rpc.common;

/**
 * Created by jiang on 2017/5/14.
 */
public interface RpcInvocation {

    String getMethodName();

    String getClassName();

    String getRequestId();

    Class<?>[] getParameterTypes();

    Object[] getParameters();

    int getMaxExecutesCount();

}
