package com.jim.framework.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.jim")
//@EnableElasticsearchRepositories(basePackages = "com.jim.repository")
public class WebApplication {

	public static void main(String[] args) {
		System.setProperty("env","test");
		SpringApplication.run(WebApplication.class, args);
	}
}

