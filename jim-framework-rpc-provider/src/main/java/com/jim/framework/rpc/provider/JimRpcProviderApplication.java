package com.jim.framework.rpc.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.jim.framework.rpc.provider")
public class JimRpcProviderApplication {

	public static void main(String[] args) throws InterruptedException {

		SpringApplication.run(JimRpcProviderApplication.class, args);

	}
}
