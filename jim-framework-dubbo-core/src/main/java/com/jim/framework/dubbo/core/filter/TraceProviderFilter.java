package com.jim.framework.dubbo.core.filter;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.jim.framework.dubbo.core.context.RpcTraceContext;
import com.jim.framework.dubbo.core.context.ZipkinCollectorConfigurationFactory;
import com.jim.framework.dubbo.core.utils.IdUtils;
import com.jim.framework.dubbo.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/*
* 服务端日志过滤器
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
@Activate
public class TraceProviderFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(!RpcTraceContext.getTraceConfig().isEnabled()){
            return invoker.invoke(invocation);
        }

        Map<String, String> attaches = invocation.getAttachments();
        if (!attaches.containsKey(RpcTraceContext.TRACE_ID_KEY)){
            return invoker.invoke(invocation);
        }

        Long traceId = Long.valueOf(attaches.get(RpcTraceContext.TRACE_ID_KEY));
        Long spanId = Long.valueOf(attaches.get(RpcTraceContext.SPAN_ID_KEY));

        attaches.remove(RpcTraceContext.TRACE_ID_KEY);
        attaches.remove(RpcTraceContext.SPAN_ID_KEY);
        RpcTraceContext.start();
        RpcTraceContext.setTraceId(traceId);
        RpcTraceContext.setParentId(spanId);
        RpcTraceContext.setSpanId(IdUtils.get());

        ZipkinCollectorConfigurationFactory zipkinCollectorConfigurationFactory=
                SpringContextUtils.getApplicationContext().getBean(ZipkinCollectorConfigurationFactory.class);
        Tracer tracer= zipkinCollectorConfigurationFactory.getTracing().tracer();

        TraceContext traceContext= TraceContext.newBuilder()
                .traceId(RpcTraceContext.getTraceId())
                .parentId(RpcTraceContext.getParentId())
                .spanId(RpcTraceContext.getSpanId())
                .sampled(true)
                .build();
        Span span = tracer.toSpan(traceContext).start();

        logger.info("provider:traceId={},parentId={},spanId={}",
                span.context().traceId(),
                span.context().parentId(),
                span.context().spanId());

        Result result = null;
        try {
            result = invoker.invoke(invocation);
        }
        finally {
            span.finish();
        }

        return result;

    }
}
