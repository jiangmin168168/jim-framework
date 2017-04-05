package com.jim.framework.web;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication
@ComponentScan("com.jim")
//@EnableElasticsearchRepositories(basePackages = "com.jim.repository")
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}
}

