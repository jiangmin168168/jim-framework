package com.jim.framework.rpc.config;

import com.jim.framework.rpc.utils.ApplicationContextUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2017/7/2/002.
 */
@Configuration
public class RpcConfiguration {
    @Bean
    public ApplicationContextUtils applicationContextUtils(){
        return new ApplicationContextUtils();
    }
}
