package com.jim.framework.activemq;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class ActivemqApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(ActivemqApplication.class).web(true).run(args);
	}
}
