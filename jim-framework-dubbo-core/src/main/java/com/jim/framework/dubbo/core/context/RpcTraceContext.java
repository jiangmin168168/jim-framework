package com.jim.framework.dubbo.core.context;

import com.jim.framework.dubbo.core.trace.config.TraceConfig;

/*
* 日志追踪上下文
* 主要结合zipken
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public class RpcTraceContext {

    private static ThreadLocal<Long> TRACE_ID = new InheritableThreadLocal<>();

    private static ThreadLocal<Long> SPAN_ID = new InheritableThreadLocal<>();

    private static ThreadLocal<Long> PARENT_ID = new InheritableThreadLocal<>();

    public static final String TRACE_ID_KEY = "traceId";

    public static final String SPAN_ID_KEY = "spanId";

    private static TraceConfig traceConfig;

    public static TraceConfig getTraceConfig(){
        return traceConfig;
    }

    public static void setTraceConfig(TraceConfig config){
        traceConfig=config;
    }

    private RpcTraceContext(){}

    public static void setTraceId(Long traceId){
        TRACE_ID.set(traceId);
    }

    public static Long getTraceId(){
        return TRACE_ID.get();
    }

    public static Long getSpanId() {
        return SPAN_ID.get();
    }

    public static void setSpanId(Long spanId) {
        SPAN_ID.set(spanId);
    }

    public static Long getParentId() {
        return PARENT_ID.get();
    }

    public static void setParentId(Long parentId) {
        PARENT_ID.set(parentId);
    }


    public static void clear(){
        TRACE_ID.remove();
        SPAN_ID.remove();
    }

    public static void init(TraceConfig traceConfig) {
        setTraceConfig(traceConfig);
    }

    public static void start(){
        clear();
    }
}
