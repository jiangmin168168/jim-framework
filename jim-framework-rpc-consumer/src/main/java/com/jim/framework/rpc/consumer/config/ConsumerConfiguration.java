package com.jim.framework.rpc.consumer.config;

import com.jim.framework.rpc.beans.BeanPostPrcessorReference;
import com.jim.framework.rpc.config.ReferenceConfig;
import com.jim.framework.rpc.utils.ApplicationContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"com.jim.framework.rpc.consumer","com.jim.framework.rpc"})
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
        referenceConfig.setRegistryHost("192.168.21.128");
        referenceConfig.setRegistryPort(8500);
        return referenceConfig;
    }

    @Bean
    public ApplicationContextUtils applicationContextUtils(){
        return new ApplicationContextUtils();
    }

}
