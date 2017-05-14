package com.jim.framework.rpc.client;

import com.jim.framework.rpc.common.*;
import com.jim.framework.rpc.proxy.ResponseFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClientInvoker extends AbstractInvoker<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientInvoker.class);

    private ConcurrentHashMap<String, ResponseFuture> pendingRPC = new ConcurrentHashMap<>();

    private volatile Channel channel;

    private final ThreadLocal<RpcRequest> rpcRequestThreadLocal=new InheritableThreadLocal<>();

    public Channel getChannel() {
        return channel;
    }

    public RpcClientInvoker(Map<String,RpcFilter> filterMap){
        super(null,filterMap);
    }

    public void setRpcRequest(RpcRequest rpcRequest){
        this.rpcRequestThreadLocal.set(rpcRequest);
    }

    public RpcRequest getRpcRequest(){
        return this.rpcRequestThreadLocal.get();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        String requestId = response.getRequestId();
        ResponseFuture responseFuture = pendingRPC.get(requestId);
        if (responseFuture != null) {
            pendingRPC.remove(requestId);
            responseFuture.done(response);
        }
    }

    @Override
    public ResponseFuture invoke(RpcInvocation invocation) {
        RpcRequest request=this.getRpcRequest();
        ResponseFuture responseFuture = new ResponseFuture(request);
        pendingRPC.put(request.getRequestId(), responseFuture);
        channel.writeAndFlush(request);
        return responseFuture;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

}

