package com.jim.framework.cloud.registry;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class JimFrameworkCloudRegistryApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(JimFrameworkCloudRegistryApplication.class).web(true).run(args);
	}
}
