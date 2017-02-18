package com.jim.config;

import com.jim.filter.TimeFilter;
import com.jim.interceptor.PermissionInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by jiang on 2016/12/22.
 */
@Configuration
@ComponentScan("com.jim")
public class WebApplicationConfig extends WebMvcConfigurerAdapter {
    @Bean
    public FilterRegistrationBean timeFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        TimeFilter timeFilter=new TimeFilter();
        registrationBean.setFilter(timeFilter);

        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new PermissionInterceptor()).addPathPatterns("/student/**");

    }
}
