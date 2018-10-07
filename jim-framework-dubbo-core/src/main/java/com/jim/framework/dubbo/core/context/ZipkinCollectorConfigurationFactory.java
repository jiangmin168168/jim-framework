package com.jim.framework.dubbo.core.context;

import brave.Tracing;
import com.google.common.base.Objects;
import com.jim.framework.dubbo.core.trace.config.TraceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiangmin on 2018/10/7.
 */
@Component
public class ZipkinCollectorConfigurationFactory {

    private final AbstractZipkinCollectorConfiguration zipkinCollectorConfiguration;

    @Autowired
    public ZipkinCollectorConfigurationFactory(TraceConfig traceConfig){
        if(Objects.equal("kafka", traceConfig.getZipkinSendType())){
            zipkinCollectorConfiguration=new KafkaZipkinCollectorConfiguration(
                    traceConfig.getApplicationName(),
                    traceConfig.getZipkinUrl(),
                    traceConfig.getZipkinKafkaTopic());
        }
        else {
            zipkinCollectorConfiguration = new HttpZipkinCollectorConfiguration(
                    traceConfig.getApplicationName(),
                    traceConfig.getZipkinUrl());
        }
    }

    public Tracing getTracing(){
        return this.zipkinCollectorConfiguration.getTracing();
    }

}
