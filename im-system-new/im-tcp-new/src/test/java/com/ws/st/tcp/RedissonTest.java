package com.ws.st.tcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

public class RedissonTest {

    private RedissonClient redissonClient;

    @BeforeEach
    public void init(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        redissonClient = Redisson.create(config);

    }
    @Test
    @DisplayName("Redisson 功能测试")
    public void demo01(){
        RBucket<Object> im = redissonClient.getBucket("im");
    System.out.println(im.get());
    im.set("im");
    System.out.println(im.get());
    }
}
