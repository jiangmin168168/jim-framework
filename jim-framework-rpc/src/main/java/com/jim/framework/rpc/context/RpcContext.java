package com.jim.framework.rpc.context;

import com.jim.framework.rpc.proxy.ResponseFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiang on 2017/5/15.
 */
public class RpcContext {

    private ResponseFuture responseFuture;

    public ResponseFuture getResponseFuture() {
        return responseFuture;
    }

    private Map<String,ResponseFuture> responseFutureMap=new HashMap<>();

    public void setResponseFuture(ResponseFuture responseFuture) {
        System.out.println("current thread id:"+Thread.currentThread().getId());
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

    private RpcContext() {
    }
}
