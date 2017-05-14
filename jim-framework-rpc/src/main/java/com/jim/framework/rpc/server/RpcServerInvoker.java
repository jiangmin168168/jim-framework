package com.jim.framework.rpc.server;

import com.jim.framework.rpc.common.*;
import com.jim.framework.rpc.exception.RpcException;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RpcServerInvoker extends AbstractInvoker<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public RpcServerInvoker(Map<String, Object> handlerMap, Map<String,RpcFilter> filterMap) {
        super(handlerMap,filterMap);
        this.handlerMap=handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) {

        RpcInvoker rpcInvoker=this.buildInvokerChain(this);
        RpcResponse response=(RpcResponse) rpcInvoker.invoke(this.buildRpcInvocation(rpcRequest));
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public RpcResponse invoke(RpcInvocation invocation) {
        String className = invocation.getClassName();
        Object serviceBean = handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = invocation.getMethodName();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] parameters = invocation.getParameters();

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        try {
            Object result= serviceFastMethod.invoke(serviceBean, parameters);
            RpcResponse rpcResponse=new RpcResponse();
            rpcResponse.setResult(result);
            rpcResponse.setRequestId(invocation.getRequestId());
            return rpcResponse;
        } catch (InvocationTargetException e) {
            throw new RpcException(e);
        }
    }

}
