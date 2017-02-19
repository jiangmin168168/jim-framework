package com.jim.framework.annotationlock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 基于注解的锁
 * key支持SPEL表达式
 * Created by jiang on 2017/1/19.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestLockable {

    String[] key() default "";

    long maximumWaiteTime() default 2000;

    long expirationTime() default 1000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
