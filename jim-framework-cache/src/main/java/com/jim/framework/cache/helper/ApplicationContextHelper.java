package com.jim.framework.cache.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextHelper implements ApplicationContextAware {

	private static ApplicationContext ctx;

	@Override
	synchronized public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		ctx = appContext;

	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}
