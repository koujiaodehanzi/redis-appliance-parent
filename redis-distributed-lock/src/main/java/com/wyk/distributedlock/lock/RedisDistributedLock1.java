package com.wyk.distributedlock.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyankun
 * @title: RedisDistributedLock1
 * @projectName redis-appliance-parent
 * @description: TODO
 * @date 2020/1/4 15:38
 */
@Component
public class RedisDistributedLock1 {

    private ThreadLocal<Map<String, Integer>> lockers = new ThreadLocal<>();

    /**
     * 持有锁的超时时间
     */
    private static final Long HOLD_LOCK_TIMEOUT = 30000L;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean tryLock(String key){
        Map<String, Integer> currentLockers = this.currentLockers();
        Integer refCount = currentLockers.get(key);
        if (this.lock(key)){
            // 重入次数+1
            currentLockers.put(key, refCount==null?1:++refCount);
            return true;
        }

        return false;
    }

    private boolean lock(String key){
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        Boolean flag = operations.setIfAbsent(key, "", HOLD_LOCK_TIMEOUT, TimeUnit.SECONDS);
        return flag == null?false:flag;
    }

    public boolean tryUnlock(String key){
        Map<String, Integer> currentLockers = this.currentLockers();
        Integer refCount = currentLockers.get(key);
        if (refCount == null){
            return false;
        }
        // 重入次数-1
        refCount -= 1;
        if (refCount == 0){
            // 只有重入次数等于0时，才真正释放锁
            if (this.unLock(key)){
                currentLockers.remove(key);
                currentLockers.put(key, refCount);
                return true;
            }
        }else{
            currentLockers.put(key, refCount);
            return true;
        }
        return false;
    }

    private boolean unLock(String key){
        Boolean flag = redisTemplate.delete(key);
        return flag == null?false:flag;
    }

    private Map<String, Integer> currentLockers(){
        Map<String, Integer> currentLockers = this.lockers.get();
        if (currentLockers == null){
            currentLockers = new HashMap<>();
            lockers.set(currentLockers);
        }
        return currentLockers;
    }

}
