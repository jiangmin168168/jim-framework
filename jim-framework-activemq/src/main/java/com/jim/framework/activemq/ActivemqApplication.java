package com.jim.framework.activemq;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
public class ActivemqApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(ActivemqApplication.class).web(true).run(args);
	}
}
