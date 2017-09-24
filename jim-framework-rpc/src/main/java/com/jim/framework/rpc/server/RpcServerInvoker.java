package com.jim.framework.rpc.server;

import com.jim.framework.rpc.common.*;
import com.jim.framework.rpc.exception.RpcException;
import com.jim.framework.rpc.protocol.RpcMessage;
import com.jim.framework.rpc.protocol.RpcMessageHeader;
import com.jim.framework.rpc.utils.ProtoStuffSerializeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.Executor;

public class RpcServerInvoker extends AbstractInvoker<RpcMessage> {

    private final Map<String, Object> handlerMap;

    private final Executor executor;

    public RpcServerInvoker(Map<String, Object> handlerMap, Map<String,RpcFilter> filterMap,Executor executor) {
        super(handlerMap,filterMap);
        this.handlerMap=handlerMap;
        this.executor=executor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMessage message) {

        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                RpcInvoker rpcInvoker=RpcServerInvoker.this.buildInvokerChain(RpcServerInvoker.this);
                RpcResponse response=(RpcResponse) rpcInvoker.invoke(RpcServerInvoker.this.buildRpcInvocation((RpcRequest) message.getMessageBody()));
                RpcMessage responseMessage=new RpcMessage();
                byte[] data = ProtoStuffSerializeUtil.serialize(response);
                RpcMessageHeader messageHeader=new RpcMessageHeader();
                messageHeader.setLength(data.length);
                responseMessage.setMessageHeader(messageHeader);
                responseMessage.setMessageBody(response);
                channelHandlerContext.writeAndFlush(responseMessage);
            }
        });

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
