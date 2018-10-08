package com.jim.framework.dubbo.core.filter;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.remoting.exchange.ResponseCallback;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.protocol.dubbo.FutureAdapter;
import com.alibaba.dubbo.rpc.support.RpcUtils;
import com.jim.framework.dubbo.core.context.RpcTraceContext;
import com.jim.framework.dubbo.core.context.ZipkinCollectorConfigurationFactory;
import com.jim.framework.dubbo.core.utils.IdUtils;
import com.jim.framework.dubbo.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/*
* 消费端日志过滤器
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
@Activate
public class TraceConsumerFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(!RpcTraceContext.getTraceConfig().isEnabled()){
            return invoker.invoke(invocation);
        }

        ZipkinCollectorConfigurationFactory zipkinCollectorConfigurationFactory=
                SpringContextUtils.getApplicationContext().getBean(ZipkinCollectorConfigurationFactory.class);
        Tracer tracer= zipkinCollectorConfigurationFactory.getTracing().tracer();

        if(null==RpcTraceContext.getTraceId()){
            RpcTraceContext.start();
            RpcTraceContext.setTraceId(IdUtils.get());
            RpcTraceContext.setParentId(null);
            RpcTraceContext.setSpanId(IdUtils.get());
        }
        else {
            RpcTraceContext.setParentId(RpcTraceContext.getSpanId());
            RpcTraceContext.setSpanId(IdUtils.get());
        }
        TraceContext traceContext= TraceContext.newBuilder()
                .traceId(RpcTraceContext.getTraceId())
                .parentId(RpcTraceContext.getParentId())
                .spanId(RpcTraceContext.getSpanId())
                .sampled(true)
                .build();

        Span span=tracer.toSpan(traceContext).start();

        invocation.getAttachments().put(RpcTraceContext.TRACE_ID_KEY, String.valueOf(span.context().traceId()));
        invocation.getAttachments().put(RpcTraceContext.SPAN_ID_KEY, String.valueOf(span.context().spanId()));
        logger.info("consumer:traceId={},parentId={},spanId={}",
                span.context().traceId(),
                span.context().parentId(),
                span.context().spanId());

        RpcContext rpcContext = RpcContext.getContext();
        boolean isAsync=false;
        Future<Object> future = rpcContext.getFuture();
        if (future instanceof FutureAdapter) {
            isAsync = true;
            ((FutureAdapter) future).getFuture().setCallback(new AsyncSpanCallback(span));
        }

        Result result = null;
        boolean isOneway = RpcUtils.isOneway(invoker.getUrl(), invocation);
        try {
            result = invoker.invoke(invocation);
        }
        finally {
            if(isOneway) {
                span.flush();
            }
            else if(!isAsync) {
                span.finish();
            }
        }

        return result;
    }

    private class AsyncSpanCallback implements ResponseCallback{

        private Span span;

        public AsyncSpanCallback(Span span){
            this.span=span;
        }

        @Override
        public void done(Object o) {
            span.finish();
        }

        @Override
        public void caught(Throwable throwable) {
            span.finish();
        }
    }
}
