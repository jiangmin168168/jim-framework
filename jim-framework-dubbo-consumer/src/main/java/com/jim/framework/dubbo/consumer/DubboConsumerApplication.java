package com.jim.framework.dubbo.consumer;

import com.jim.framework.dubbo.core.annotation.EnableTraceAutoConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableTraceAutoConfigurationProperties
@ImportResource("classpath:applicationContext-dubbo-consumer.xml")
@ComponentScan("com.jim")
public class DubboConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboConsumerApplication.class, args);
	}
}
