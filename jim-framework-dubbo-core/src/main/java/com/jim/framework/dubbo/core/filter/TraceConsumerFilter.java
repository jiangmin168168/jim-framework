package com.jim.framework.dubbo.core.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.google.common.base.Stopwatch;
import com.jim.framework.dubbo.core.context.TraceContext;
import com.jim.framework.dubbo.core.trace.TraceAgent;
import com.jim.framework.dubbo.core.utils.IdUtils;
import com.jim.framework.dubbo.core.utils.NetworkUtils;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    //private TraceAgent traceAgent=new TraceAgent(TraceContext.getTraceConfig().getZipkinUrl());

    private Span startTrace(Invoker<?> invoker, Invocation invocation) {

        Span consumerSpan = new Span();

        Long traceId=null;
        long id = IdUtils.get();
        consumerSpan.setId(id);
        if(null==TraceContext.getTraceId()){
            TraceContext.start();
            traceId=id;
        }
        else {
            traceId=TraceContext.getTraceId();
        }

        consumerSpan.setTrace_id(traceId);
        consumerSpan.setParent_id(TraceContext.getSpanId());
        consumerSpan.setName(TraceContext.getTraceConfig().getApplicationName());
        long timestamp = System.currentTimeMillis()*1000;
        consumerSpan.setTimestamp(timestamp);

        consumerSpan.addToAnnotations(
                Annotation.create(timestamp, TraceContext.ANNO_CS,
                        Endpoint.create(
                                TraceContext.getTraceConfig().getApplicationName(),
                                NetworkUtils.ip2Num(NetworkUtils.getSiteIp()),
                                TraceContext.getTraceConfig().getServerPort() )));

        Map<String, String> attaches = invocation.getAttachments();
        attaches.put(TraceContext.TRACE_ID_KEY, String.valueOf(consumerSpan.getTrace_id()));
        attaches.put(TraceContext.SPAN_ID_KEY, String.valueOf(consumerSpan.getId()));
        return consumerSpan;
    }

    private void endTrace(Span span, Stopwatch watch) {

        span.addToAnnotations(
                Annotation.create(System.currentTimeMillis()*1000, TraceContext.ANNO_CR,
                        Endpoint.create(
                                span.getName(),
                                NetworkUtils.ip2Num(NetworkUtils.getSiteIp()),
                                TraceContext.getTraceConfig().getServerPort())));

        span.setDuration(watch.stop().elapsed(TimeUnit.MICROSECONDS));
        TraceAgent traceAgent=new TraceAgent(TraceContext.getTraceConfig().getZipkinUrl());

        traceAgent.send(TraceContext.getSpans());

    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(!TraceContext.getTraceConfig().isEnabled()){
            return invoker.invoke(invocation);
        }

        Stopwatch watch = Stopwatch.createStarted();
        Span span= this.startTrace(invoker,invocation);
        TraceContext.start();
        TraceContext.setTraceId(span.getTrace_id());
        TraceContext.setSpanId(span.getId());
        TraceContext.addSpan(span);
        Result result = invoker.invoke(invocation);
        this.endTrace(span,watch);

        return result;
    }
}
