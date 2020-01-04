package com.wyk.distributedlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisDistributedLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisDistributedLockApplication.class, args);
    }

}
