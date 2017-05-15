package com.jim.framework.rpc.context;

import com.jim.framework.rpc.proxy.ResponseFuture;

/**
 * Created by jiang on 2017/5/15.
 */
public class RpcContext {

    private ResponseFuture responseFuture;

    public ResponseFuture getResponseFuture() {
        return responseFuture;
    }

    public void setResponseFuture(ResponseFuture responseFuture) {
        this.responseFuture = responseFuture;
    }

    private static final ThreadLocal<RpcContext> rpcContextThreadLocal=new ThreadLocal<RpcContext>(){
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return rpcContextThreadLocal.get();
    }

    public static void removeContext() {
        rpcContextThreadLocal.remove();
    }
}
