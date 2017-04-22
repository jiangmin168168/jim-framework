package com.jim.framework.web.config;

import com.jim.framework.cache.helper.ApplicationContextHelper;
import com.jim.framework.configcenter.spring.SpringPropertyInjectSupport;
import com.jim.framework.web.filter.TimeFilter;
import com.jim.framework.web.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    @Bean
    public ApplicationContextHelper initApplicationContextHelper(){
        return new ApplicationContextHelper();
    }

    @Bean
    public static PropertyPlaceholderConfigurer properties() {
        SpringPropertyInjectSupport springPropertyInjectSupport=new SpringPropertyInjectSupport();
        springPropertyInjectSupport.setConfigNameSpaces("configcenter/"+System.getProperty("env"));
        springPropertyInjectSupport.init();
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[]
                { new ClassPathResource( "application.properties" ) };
        ppc.setLocations( resources );
        ppc.setIgnoreUnresolvablePlaceholders( true );
        return ppc;
    }

}
