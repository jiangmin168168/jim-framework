package com.jim.framework.rpc.client;

import com.jim.framework.rpc.cache.RpcClientInvokerCache;
import com.jim.framework.rpc.config.ReferenceConfig;
import com.jim.framework.rpc.exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jiang on 2017/5/21.
 */
public class RpcClientInvokerManager {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition connected = lock.newCondition();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private static ReferenceConfig referenceConfig;

    private final static RpcClientInvokerManager instance=new RpcClientInvokerManager();

    private static final Logger logger = LoggerFactory.getLogger(RpcClientInvokerManager.class);

    public void addHandler(RpcClientInvoker handler) {
        RpcClientInvokerCache.addHandler(handler);
        this.signalAvailableHandler();
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

    public RpcClientInvoker getInvoker() {
        int size = RpcClientInvokerCache.size();

        while (size <= 0) {
            try {
                InetSocketAddress remotePeer=new InetSocketAddress(this.referenceConfig.getHost(),this.referenceConfig.getPort());
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new RpcClientInitializer())
                        .option(ChannelOption.TCP_NODELAY,true)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,(int)this.referenceConfig.getConnectTimeoutMillis())

                ;

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
                    size = RpcClientInvokerCache.size();
                }
            } catch (InterruptedException e) {
                throw new RpcException(e);
            }
        }
        return RpcClientInvokerCache.get(0);
    }

    private RpcClientInvokerManager(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < RpcClientInvokerCache.size(); i++) {
                    RpcClientInvoker connectedServerHandler = RpcClientInvokerCache.get(i);
                    connectedServerHandler.close();
                }
                instance.eventLoopGroup.shutdownGracefully();
            }
        }, "RpcShutdownHook-RpcClientInvoker"));
    }

    public static RpcClientInvokerManager getInstance(ReferenceConfig config){
        referenceConfig=config;
        return instance;
    }

}
