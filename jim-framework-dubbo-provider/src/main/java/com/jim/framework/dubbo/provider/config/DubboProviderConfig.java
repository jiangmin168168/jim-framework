package com.jim.framework.dubbo.provider.config;

import com.alibaba.dubbo.config.*;
import org.springframework.context.annotation.Bean;

/*
* DBUBBO配置文件
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
//@Configuration
public class DubboProviderConfig {

    @Bean
    public RegistryConfig registryConfig(){
        RegistryConfig config=new RegistryConfig();
        config.setAddress("N/A");
        return config;
    }

    @Bean
    public ApplicationConfig applicationConfig(){
        ApplicationConfig config=new ApplicationConfig();
        config.setName("dubbo-provider");
        config.setLogger("slf4j");
        return config;

    }

    @Bean
    public ReferenceConfig referenceConfig(){
        ReferenceConfig config=new ReferenceConfig();
        return config;
    }

    @Bean
    public ProtocolConfig protocolConfig(){
        ProtocolConfig config=new ProtocolConfig();
        config.setPort(9999);
        config.setName("dubbo");
        config.setAccesslog("true");
        return config;
    }

    @Bean
    public ProviderConfig providerConfig(){
        ProviderConfig config=new ProviderConfig();
        config.setFilter("traceProviderFilter");
        return config;
    }

//    @Bean
//    public ServiceBean<ProductService> productServiceServiceBean(ProductService productService){
//        ServiceBean<ProductService> productServiceServiceBean=new ServiceBean<>();
//        productServiceServiceBean.setInterface(ProductService.class);
//        productServiceServiceBean.setRef(productService);
//        return productServiceServiceBean;
//
//    }
}
