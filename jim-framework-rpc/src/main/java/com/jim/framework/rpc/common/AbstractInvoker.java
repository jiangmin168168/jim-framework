package com.jim.framework.rpc.common;

import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Map;

/**
 * Created by jiang on 2017/5/14.
 */
public abstract class AbstractInvoker<T> extends SimpleChannelInboundHandler<T> implements RpcInvoker {

    private final Map<String, Object> handlerMap;
    private final Map<String,RpcFilter> filterMap;

    protected AbstractInvoker(Map<String, Object> handlerMap, Map<String,RpcFilter> filterMap){
        this.handlerMap = handlerMap;
        this.filterMap=filterMap;
    }

    public RpcInvocation buildRpcInvocation(RpcRequest request){
        RpcInvocation rpcInvocation=new RpcInvocation() {
            @Override
            public String getMethodName() {
                return request.getMethodName();
            }

            @Override
            public String getClassName() {
                return request.getClassName();
            }

            @Override
            public String getRequestId() {
                return request.getRequestId();
            }

            @Override
            public Class<?>[] getParameterTypes() {
                return request.getParameterTypes();
            }

            @Override
            public Object[] getParameters() {
                return request.getParameters();
            }

            @Override
            public int getMaxExecutesCount() {
                return request.getMaxExecutesCount();
            }

            @Override
            public Map<String, Object> getContextParameters() {
                return request.getContextParameters();
            }
        };
        return rpcInvocation;
    }

    public RpcInvoker buildInvokerChain(final RpcInvoker invoker) {
        RpcInvoker last = invoker;
        List<RpcFilter> filters = Lists.newArrayList(this.filterMap.values());

        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i --) {
                final RpcFilter filter = filters.get(i);
                final RpcInvoker next = last;
                last = new RpcInvoker() {
                    @Override
                    public Object invoke(RpcInvocation invocation) {
                        return filter.invoke(next, invocation);
                    }
                };
            }
        }
        return last;
    }

    protected abstract void channelRead0(ChannelHandlerContext channelHandlerContext, T t);

    public abstract Object invoke(RpcInvocation invocation);

}
