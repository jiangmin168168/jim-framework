package com.jim.framework.rpc.proxy;

import com.jim.framework.rpc.client.RpcClientHandler;
import com.jim.framework.rpc.client.RpcClientInitializer;
import com.jim.framework.rpc.common.RpcRequest;
import com.jim.framework.rpc.config.ReferenceConfig;
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

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private CopyOnWriteArrayList<RpcClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();

    private ReferenceConfig referenceConfig;

    public RpcProxy(Class<T> clazz,ReferenceConfig referenceConfig) {
        this.clazz = clazz;
        this.referenceConfig=referenceConfig;

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < connectedHandlers.size(); i++) {
                    RpcClientHandler connectedServerHandler = connectedHandlers.get(i);
                    connectedServerHandler.close();
                }
                RpcProxy.this.eventLoopGroup.shutdownGracefully();
            }
        }, "RpcShutdownHook-RpcClientHandler"));
    }

    private void addHandler(RpcClientHandler handler) {
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

    private RpcClientHandler getHandler() {
        CopyOnWriteArrayList<RpcClientHandler> handlers = (CopyOnWriteArrayList<RpcClientHandler>) this.connectedHandlers.clone();
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
                            RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                            addHandler(handler);
                        }
                    }
                });
                boolean available = waitingForHandler();
                if (available) {
                    handlers = (CopyOnWriteArrayList<RpcClientHandler>) this.connectedHandlers.clone();
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

        RpcClientHandler handler = this.getHandler();
        ResponseFuture responseFuture = handler.sendRequest(request);
        return responseFuture.get();
    }
}
