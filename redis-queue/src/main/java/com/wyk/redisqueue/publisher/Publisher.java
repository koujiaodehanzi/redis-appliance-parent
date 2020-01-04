package com.wyk.redisqueue.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 抠脚的汉子
 * @title: Publisher
 * @projectName redis-appliance-parent
 * @description: TODO
 * @date 2020/1/4 14:49
 */
@Service
public class Publisher{

    @Autowired
    private RedisTemplate redisTemplate;

    public void publish(Object msg){
        redisTemplate.convertAndSend("redis-channel",msg);
    }

}
