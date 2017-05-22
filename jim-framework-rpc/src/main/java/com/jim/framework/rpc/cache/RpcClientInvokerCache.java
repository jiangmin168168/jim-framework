package com.jim.framework.rpc.cache;

import com.jim.framework.rpc.client.RpcClientInvoker;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jiang on 2017/5/22.
 */
public class RpcClientInvokerCache {

    private static CopyOnWriteArrayList<RpcClientInvoker> connectedHandlers = new CopyOnWriteArrayList<>();

    public static CopyOnWriteArrayList<RpcClientInvoker> getConnectedHandlersClone(){
        return (CopyOnWriteArrayList<RpcClientInvoker>) RpcClientInvokerCache.getConnectedHandlers().clone();
    }

    public static void addHandler(RpcClientInvoker handler) {
        CopyOnWriteArrayList<RpcClientInvoker> newHandlers = getConnectedHandlersClone();
        newHandlers.add(handler);
        connectedHandlers=newHandlers;
    }

    public static CopyOnWriteArrayList<RpcClientInvoker> getConnectedHandlers(){
        return connectedHandlers;
    }

    public static RpcClientInvoker get(int i){
        return connectedHandlers.get(i);
    }

    public static int size(){
        return connectedHandlers.size();
    }

    public static void clear(){
        CopyOnWriteArrayList<RpcClientInvoker> newHandlers = getConnectedHandlersClone();
        newHandlers.clear();
        connectedHandlers=newHandlers;
    }
}
