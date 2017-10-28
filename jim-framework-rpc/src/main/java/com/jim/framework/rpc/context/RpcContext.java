package com.jim.framework.rpc.context;

import com.google.common.collect.Maps;
import com.jim.framework.rpc.proxy.ResponseFuture;

import java.util.Map;

/**
 * Created by jiang on 2017/5/15.
 */
public class RpcContext {

    private ResponseFuture responseFuture;

    private Map<String,Object> contextParameters;

    public ResponseFuture getResponseFuture() {
        return responseFuture;
    }

    public void addContextParameter(String key,Object value){
        this.getContextParameters().put(key,value);
    }

    public Object getContextParameter(String key){
        return this.getContextParameters().get(key);
    }

    public void setResponseFuture(ResponseFuture responseFuture) {
        System.out.println("current thread id:"+Thread.currentThread().getId());
        this.responseFuture = responseFuture;
    }

    public Map<String, Object> getContextParameters() {
        return contextParameters;
    }

    public void setContextParameters(Map<String, Object> contextParameters) {
        this.contextParameters = contextParameters;
    }

    private static final ThreadLocal<RpcContext> rpcContextThreadLocal=new ThreadLocal<RpcContext>(){
        @Override
        protected RpcContext initialValue() {
            RpcContext context= new RpcContext();
            context.setContextParameters(Maps.newHashMap());
            return context;
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
