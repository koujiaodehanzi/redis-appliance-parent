package com.wyk.redisqueue.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * @author 抠脚的汉子
 * @title: RedisListener
 * @projectName redis-appliance-parent
 * @description: TODO
 * @date 2020/1/4 14:27
 */
@Component
public class RedisListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisListener.class);

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 防止消费消息出现乱序
        Lock lock = redisLockRegistry.obtain("lock");

        try {
            LOGGER.info("pattern:{},消息通道:{}",new String(pattern),message.getChannel());
            // 序列化和反序列化要一致
            RedisSerializer serializer=new GenericJackson2JsonRedisSerializer();
            LOGGER.debug("消息内容:{}",serializer.deserialize(message.getBody()));
            // TODO doSomething


        }catch (Exception e){
            LOGGER.error("exception is ", e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
