package com.jim.framework.dubbo.core.context;

import brave.Tracing;
import brave.sampler.Sampler;
import zipkin2.Span;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;

import java.util.concurrent.TimeUnit;

/*
* zipkin收集器配置基类
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public abstract class AbstractZipkinCollectorConfiguration {

    private Tracing tracing;

    private String zipkinUrl;

    private String serviceName;

    private String topic;

    public String getTopic() {
        return topic;
    }

    protected String getZipkinUrl() {
        return zipkinUrl;
    }

    public AbstractZipkinCollectorConfiguration(String serviceName,String zipkinUrl,String topic){
        this.zipkinUrl=zipkinUrl;
        this.serviceName=serviceName;
        this.topic=topic;
        this.tracing=this.tracing();
    }

    public abstract Sender getSender();

    protected AsyncReporter<Span> spanReporter() {
        return AsyncReporter
                .builder(getSender())
                .closeTimeout(500, TimeUnit.MILLISECONDS)
                .build(SpanBytesEncoder.JSON_V2);
    }

    protected Tracing tracing() {
        this.tracing= Tracing
                .newBuilder()
                .localServiceName(this.serviceName)
                .sampler(Sampler.ALWAYS_SAMPLE)
                .spanReporter(spanReporter())
                .build();
        return this.tracing;
    }

    protected Tracing getTracing(){
        return this.tracing;
    }
}
