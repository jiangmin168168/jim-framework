package com.jim.framework.annotationlock.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by jiang on 2017/2/19.
 */
@Service
public class RedissonContext {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String pass;

    private RedissonClient redisson;

    @PostConstruct
    public void init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(this.host + ":" + this.port)
               // .setPassword(this.pass)
        .setConnectionPoolSize(10)
        .setConnectTimeout(10000)
        ;
        redisson = Redisson.create(config);
    }

    public RedissonClient getRedisson() {
        return redisson;
    }
}
