package com.fast.kaca.search.web.utils;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * @author sys
 * @date 2019/1/16
 **/
@Component
public class JedisTools {

    @Resource
    private JedisPool jedisPool;

    public String get(String name) {
        Jedis jedis = jedisPool.getResource();
        return jedis.get(name);
    }

    public void set(String name, String value, int expiredSeconds) {
        Jedis jedis = jedisPool.getResource();
        jedis.setex(name, expiredSeconds, value);
    }

    public void delete(String name) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(name);
    }

}
