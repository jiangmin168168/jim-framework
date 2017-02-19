package com.jim.framework.web.config;

import com.jim.framework.web.filter.TimeFilter;
import com.jim.framework.web.interceptor.PermissionInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by jiang on 2016/12/22.
 */
@Configuration
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
