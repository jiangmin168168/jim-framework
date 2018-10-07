package com.jim.framework.dubbo.provider;

import com.jim.framework.dubbo.core.annotation.EnableTraceAutoConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableTraceAutoConfigurationProperties
@ComponentScan(basePackages = {"com.jim.framework.dubbo.core"})
@ImportResource({"classpath:applicationContext-dubbo-consumer.xml","classpath:applicationContext-dubbo-provider.xml"})
public class DubboProviderApplication {

	private static final Logger logger = LoggerFactory.getLogger(DubboProviderApplication.class);

	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx = new SpringApplicationBuilder()
				.sources(DubboProviderApplication.class)
				.web(false)
				.run(args);

		CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
		closeLatch.await();
	}
}
