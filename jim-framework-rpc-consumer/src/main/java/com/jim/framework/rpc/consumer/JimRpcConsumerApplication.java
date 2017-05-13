package com.jim.framework.rpc.consumer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class JimRpcConsumerApplication {

	public static void main(String[] args) throws InterruptedException {

		new SpringApplicationBuilder(JimRpcConsumerApplication.class).web(true).run(args);

	}
}
