package com.jim.framework.rpc.proxy;

import com.jim.framework.rpc.client.RpcClientInitializer;
import com.jim.framework.rpc.client.RpcClientInvoker;
import com.jim.framework.rpc.common.RpcInvoker;
import com.jim.framework.rpc.common.RpcRequest;
import com.jim.framework.rpc.config.ReferenceConfig;
import com.jim.framework.rpc.context.RpcContext;
import com.jim.framework.rpc.exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RpcProxy <T> implements InvocationHandler {

    private Class<T> clazz;

    private boolean isSync=true;

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private CopyOnWriteArrayList<RpcClientInvoker> connectedHandlers = new CopyOnWriteArrayList<>();

    private ReferenceConfig referenceConfig;

    public RpcProxy(Class<T> clazz,ReferenceConfig referenceConfig,boolean isSync) {
        this.clazz = clazz;
        this.referenceConfig=referenceConfig;
        this.isSync=isSync;

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < connectedHandlers.size(); i++) {
                    RpcClientInvoker connectedServerHandler = connectedHandlers.get(i);
                    connectedServerHandler.close();
                }
                RpcProxy.this.eventLoopGroup.shutdownGracefully();
            }
        }, "RpcShutdownHook-RpcClientInvoker"));
    }

    private void addHandler(RpcClientInvoker handler) {
        connectedHandlers.add(handler);
        signalAvailableHandler();
    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(this.referenceConfig.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
        finally {
            lock.unlock();
        }
    }

    private RpcClientInvoker getInvoker() {
        CopyOnWriteArrayList<RpcClientInvoker> handlers = (CopyOnWriteArrayList<RpcClientInvoker>) this.connectedHandlers.clone();
        int size = handlers.size();

        while (size <= 0) {
            try {
                InetSocketAddress remotePeer=new InetSocketAddress(this.referenceConfig.getHost(),this.referenceConfig.getPort());
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new RpcClientInitializer());

                ChannelFuture channelFuture = b.connect(remotePeer);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            RpcClientInvoker handler = channelFuture.channel().pipeline().get(RpcClientInvoker.class);
                            addHandler(handler);
                        }
                    }
                });
                boolean available = waitingForHandler();
                if (available) {
                    handlers = (CopyOnWriteArrayList<RpcClientInvoker>) this.connectedHandlers.clone();
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                throw new RpcException(e);
            }
        }
        return handlers.get(0);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        RpcClientInvoker invoker = this.getInvoker();
        invoker.setRpcRequest(request);

        RpcInvoker rpcInvoker=invoker.buildInvokerChain(invoker);
        ResponseFuture response=(ResponseFuture) rpcInvoker.invoke(invoker.buildRpcInvocation(request));

        if(isSync){
            return response.get();
        }
        else {
            RpcContext.getContext().setResponseFuture(response);
            return null;
        }
    }
}
