package com.jim.framework.cache.redis;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 缓存方法注册接口
 */
public interface InvocationRegistry {

	void registerInvocation(Object invokedBean, Method invokedMethod, Object[] invocationArguments, Set<String> cacheNames);

}