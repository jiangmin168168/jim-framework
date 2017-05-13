package com.jim.framework.rpc.consumer.config;

import com.jim.framework.rpc.beans.BeanPostPrcessorReference;
import com.jim.framework.rpc.config.ReferenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.jim.framework.rpc.consumer")
@Configuration
public class ConsumerConfiguration {

    @Bean
    @Autowired
    public BeanPostPrcessorReference beanPostPrcessorReference(ReferenceConfig referenceConfig){
        return new BeanPostPrcessorReference(referenceConfig);
    }

    @Bean
    public ReferenceConfig referenceConfig(){
        ReferenceConfig referenceConfig=new ReferenceConfig();
        referenceConfig.setHost("127.0.0.1");
        referenceConfig.setPort(9988);
        return referenceConfig;
    }

}
