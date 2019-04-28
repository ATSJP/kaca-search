package com.fast.kaca.search.web.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 日终解决redis自动释放连接问题
 * // TODO 此方式不好 会产生应用及redis一些开销
 * @author sys
 * @date 2019/4/28
 **/
@Component
public class Quartz {

    private Logger logger = LoggerFactory.getLogger(Quartz.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/10 * * * * *")
    public void timer() {
        logger.info("redis hear beat start");
        redisTemplate.opsForValue().get("heartbeat");
        logger.info("redis hear beat end");
    }

}
