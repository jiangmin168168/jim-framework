package com.jim.framework.dubbo.core.annotation;

import java.lang.annotation.*;

/*
* 日志追踪是否启用的注解
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableTraceAutoConfigurationProperties {
}
