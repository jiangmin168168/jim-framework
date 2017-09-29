package com.jim.framework.rpc.cache;

import com.jim.framework.rpc.client.RpcClientInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jiang on 2017/5/22.
 */
public class RpcClientInvokerCache {

    private static final Logger logger= LoggerFactory.getLogger(RpcClientInvokerCache.class);

    private static CopyOnWriteArrayList<RpcClientInvoker> connectedHandlers = new CopyOnWriteArrayList<>();

    private static CopyOnWriteArrayList<RpcClientInvoker> notConnectedHandlers = new CopyOnWriteArrayList<>();

    public static CopyOnWriteArrayList<RpcClientInvoker> getConnectedHandlersClone(){
        return (CopyOnWriteArrayList<RpcClientInvoker>) RpcClientInvokerCache.getConnectedHandlers().clone();
    }
    public static CopyOnWriteArrayList<RpcClientInvoker> getNotConnectedHandlersClone(){
        return (CopyOnWriteArrayList<RpcClientInvoker>) RpcClientInvokerCache.getNotConnectedHandlers().clone();
    }
    public static void addHandler(RpcClientInvoker handler) {
        CopyOnWriteArrayList<RpcClientInvoker> connectedHandlersClone = getConnectedHandlersClone();
        connectedHandlersClone.add(handler);
        connectedHandlers=connectedHandlersClone;
        logger.info("handler added:localAddress{},remoteAddress{}",handler.getChannel().localAddress(),handler.getChannel().remoteAddress());
    }

    public static void removeHandler(RpcClientInvoker handler) {

        CopyOnWriteArrayList<RpcClientInvoker> connectedHandlersClone = getConnectedHandlersClone();
        connectedHandlersClone.remove(handler);
        connectedHandlers=connectedHandlersClone;

        CopyOnWriteArrayList<RpcClientInvoker> notConnectedHandlersClone = getNotConnectedHandlersClone();
        notConnectedHandlersClone.add(handler);
        notConnectedHandlers=notConnectedHandlersClone;
        logger.info("handler removed:localAddress{},remoteAddress{}",handler.getChannel().localAddress(),handler.getChannel().remoteAddress());
    }

    public static void clearNotConnectedHandler() {
        CopyOnWriteArrayList<RpcClientInvoker> notConnectedHandlersClone = getNotConnectedHandlersClone();
        notConnectedHandlersClone.clear();
        notConnectedHandlers=notConnectedHandlersClone;
    }

    public static CopyOnWriteArrayList<RpcClientInvoker> getConnectedHandlers(){
        return connectedHandlers;
    }

    public static CopyOnWriteArrayList<RpcClientInvoker> getNotConnectedHandlers(){
        return notConnectedHandlers;
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
