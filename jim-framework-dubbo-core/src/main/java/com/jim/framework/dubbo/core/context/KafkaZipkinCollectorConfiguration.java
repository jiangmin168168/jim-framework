package com.jim.framework.dubbo.core.context;

import zipkin2.codec.Encoding;
import zipkin2.reporter.Sender;
import zipkin2.reporter.kafka11.KafkaSender;

/**
 * Created by jiangmin on 2018/10/7.
 */
public class KafkaZipkinCollectorConfiguration extends AbstractZipkinCollectorConfiguration {

    private String topic;

    public KafkaZipkinCollectorConfiguration(String serviceName,String zipkinUrl,String topic) {
        super(serviceName,zipkinUrl);
        this.topic=topic;
    }

    @Override
    public Sender getSender() {

        return KafkaSender.newBuilder().bootstrapServers(super.getZipkinUrl()).topic(this.topic).encoding(Encoding.JSON).build();
    }
}
