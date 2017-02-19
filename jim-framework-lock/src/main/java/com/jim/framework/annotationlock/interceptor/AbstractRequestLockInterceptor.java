package com.jim.framework.annotationlock.interceptor;

import com.google.common.base.Joiner;
import com.jim.framework.annotationlock.annotation.RequestLockable;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 基于注解的请求锁
 * 扫描特定包下面标记了特定注解的方法，实施锁机制
 * 获取锁依赖于子类，可以是redis锁，也可以是其它类型的锁
 * Created by jiang on 2017/1/19.
 */
public abstract class AbstractRequestLockInterceptor {

    private static Logger logger= LoggerFactory.getLogger(AbstractRequestLockInterceptor.class);

    private String getLockKey(Method method,String targetName, String methodName, String[] keys, Object[] arguments) {

        StringBuilder sb = new StringBuilder();
        sb.append("lock.").append(targetName).append(".").append(methodName);
        if(keys != null) {
            String keyStr = Joiner.on(".").skipNulls().join(keys);
            if(!StringUtils.isBlank(keyStr)) {
                LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
                String[] parameters =discoverer.getParameterNames(method);
                ExpressionParser parser = new SpelExpressionParser();
                Expression expression = parser.parseExpression(keyStr);
                EvaluationContext context = new StandardEvaluationContext();
                int length = parameters.length;
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        context.setVariable(parameters[i], arguments[i]);
                    }
                }
                String keysValue = expression.getValue(context, String.class);
                sb.append("#").append(keysValue);
            }
        }
        return sb.toString();
    }

    protected abstract Lock getLock(String key);

    protected abstract boolean tryLock(long waitTime, long leaseTime, TimeUnit unit,Lock lock) throws InterruptedException;

    @Pointcut("@annotation(com.jim.framework.annotationlock.annotation.RequestLockable)")
    public void pointcut(){}

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable{
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String targetName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] arguments = point.getArgs();

        if (method != null && method.isAnnotationPresent(RequestLockable.class)) {
            logger.info("RequestLockable doAround start");
            RequestLockable requestLockable = method.getAnnotation(RequestLockable.class);

            String requestLockKey = getLockKey(method,targetName, methodName, requestLockable.key(), arguments);
            Lock lock=this.getLock(requestLockKey);
            boolean isLock = this.tryLock(requestLockable.maximumWaiteTime(),requestLockable.expirationTime(), requestLockable.timeUnit(),lock);
            if(isLock) {
                try {
                    logger.info("RequestLockable point.proceed start");
                    return point.proceed();
                } finally {
                    try {
                        lock.unlock();
                    }
                    catch (IllegalMonitorStateException e){
                        logger.info("not locked by current thread",e);
                    }
                }
            } else {
                logger.error("get lock error,key:{}",requestLockKey);
                //多线程场景下主线程捕获异常需要注意,不同的调用方式可能会影响异常的抛出
                throw new RuntimeException("get lock error");
            }
        }
        return point.proceed();
    }



}

