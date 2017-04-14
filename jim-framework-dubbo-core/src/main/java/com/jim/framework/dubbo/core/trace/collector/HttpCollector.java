package com.jim.framework.dubbo.core.trace.collector;

import com.github.kristofa.brave.AbstractSpanCollector;
import com.github.kristofa.brave.SpanCollectorMetricsHandler;
import com.jim.framework.dubbo.core.context.TraceContext;
import com.jim.framework.dubbo.core.trace.config.TraceConfig;
import com.twitter.zipkin.gen.SpanCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

/*
* HTTP方式将日志发送到zipkin
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public class HttpCollector extends AbstractSpanCollector {

    public HttpCollector(SpanCodec codec, SpanCollectorMetricsHandler metrics, int flushInterval) {
        super(codec, metrics, flushInterval);
    }

    public static HttpCollector create(String baseUrl, TraceConfig config, SpanCollectorMetricsHandler metrics) {
        return new HttpCollector(baseUrl, config, metrics);
    }

    HttpCollector(String baseUrl, TraceConfig config, SpanCollectorMetricsHandler metrics) {
        super(SpanCodec.JSON, metrics,  config.getFlushInterval());

    }

    @Override
    protected void sendSpans(byte[] bytes) throws IOException {
// intentionally not closing the connection, so as to use keep-alives
        HttpURLConnection connection = (HttpURLConnection) new URL(TraceContext.getTraceConfig().getZipkinUrl()+"/api/v1/spans").openConnection();
        connection.setConnectTimeout(TraceContext.getTraceConfig().getConnectTimeout());
        connection.setReadTimeout(TraceContext.getTraceConfig().getReadTimeout());
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json");
        if (TraceContext.getTraceConfig().isCompressionEnabled()) {
            connection.addRequestProperty("Content-Encoding", "gzip");
            ByteArrayOutputStream gzipped = new ByteArrayOutputStream();
            try (GZIPOutputStream compressor = new GZIPOutputStream(gzipped)) {
                compressor.write(bytes);
            }
            bytes = gzipped.toByteArray();
        }
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(bytes.length);
        connection.getOutputStream().write(bytes);

        try (InputStream in = connection.getInputStream()) {
            while (in.read() != -1) ; // skip
        } catch (IOException e) {
            try (InputStream err = connection.getErrorStream()) {
                if (err != null) { // possible, if the connection was dropped
                    while (err.read() != -1) ; // skip
                }
            }
            throw e;
        }
    }
}
