package com.jim.framework.dubbo.provider2;

import com.jim.framework.dubbo.core.annotation.EnableTraceAutoConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableTraceAutoConfigurationProperties
@ImportResource("classpath:applicationContext-dubbo-provider.xml")
public class DubboProviderApplication2 {

	private static final Logger logger = LoggerFactory.getLogger(DubboProviderApplication2.class);

	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx = new SpringApplicationBuilder()
				.sources(DubboProviderApplication2.class)
				.web(false)
				.run(args);

		CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
		closeLatch.await();
	}
}
