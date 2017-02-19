package com.jim.framework.annotationlock.interceptor;

import com.jim.framework.annotationlock.redis.RedissonContext;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Redis分布式锁实现
 * Created by jiang on 2017/1/19.
 */
@Aspect
@Component
public class RedisRequestLockInterceptor extends AbstractRequestLockInterceptor {

    @Autowired
    private RedissonContext redissonContext;

    private RedissonClient getRedissonClient(){
        return this.redissonContext.getRedisson();
    }

    @Override
    protected Lock getLock(String key) {
        return this.getRedissonClient().getLock(key);
    }

    @Override
    protected boolean tryLock(long waitTime, long leaseTime, TimeUnit unit,Lock lock) throws InterruptedException {
        return ((RLock)lock).tryLock(waitTime,leaseTime,unit);
    }
}
