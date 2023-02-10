package com.ws.st.tcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.time.Instant;

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
    @Test
    @DisplayName("Redisson map 测试")
    public void demo02(){
        RMap<String, Object> imMap = redissonClient.getMap("imMap");
        System.out.println(imMap.get("name"));
        imMap.put("name","john");
        System.out.println(imMap.get("name"));
        imMap.expire(Instant.now());
    }
    @Test
    @DisplayName("Redisson 发布订阅 测试")
    public void demo03(){
        RTopic topic = redissonClient.getTopic("topic");
        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                System.out.println("1 收到 消息 ：" + msg);
            }
        });
        RTopic topic2 = redissonClient.getTopic("topic");
        topic2.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                System.out.println("2 收到 消息 ：" + msg);
            }
        });
        topic.publish("hello world");
    }
}
