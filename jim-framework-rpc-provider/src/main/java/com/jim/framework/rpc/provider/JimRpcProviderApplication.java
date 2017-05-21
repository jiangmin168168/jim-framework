package com.jim.framework.rpc.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ContextConfiguration(classes = {ProviderConfiguration.class})
@ComponentScan(basePackages = {"com.jim.framework.rpc.provider","com.jim.framework.rpc"})
public class JimRpcProviderApplication {

	public static void main(String[] args) throws InterruptedException {

		SpringApplication.run(JimRpcProviderApplication.class, args);

	}
}
