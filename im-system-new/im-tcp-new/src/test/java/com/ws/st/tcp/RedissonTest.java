package com.ws.st.tcp;

import jodd.time.TimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class RedissonTest {

    private RedissonClient redissonClient;

    @BeforeEach
    public void init(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.3.201:6379");
        config.useSingleServer().setPassword("123456");
        config.useSingleServer().setDatabase(14);
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


    @Test
    @DisplayName("Redisson map 测试")
    public void demo04() throws InterruptedException {
        final RMapCache<String, Object> map = redissonClient.getMapCache("anyMap");

// ttl = 10 minutes,
        map.put("key1", "1000", 10, TimeUnit.SECONDS);
// ttl = 10 minutes, maxIdleTime = 10 seconds
        map.put("key2", "2000", 1, TimeUnit.MINUTES, 1, TimeUnit.MINUTES);
        map.put("key3", "3000", 10, TimeUnit.SECONDS, 10, TimeUnit.MICROSECONDS);
        map.put("key4", "4000", 1, TimeUnit.MINUTES, 1, TimeUnit.MINUTES);
        System.out.println("---------------------------------------------------------------------------");
        System.out.println(map.get("key1"));
        System.out.println(map.get("key2"));
        System.out.println(map.get("key3"));
        System.out.println(map.get("key4"));
        TimeUnit.SECONDS.sleep(15);

        System.out.println("---------------------------------------------------------------------------");
        System.out.println(map.get("key1"));
        System.out.println(map.get("key2"));
        System.out.println(map.get("key3"));
        System.out.println(map.get("key4"));

// ttl = 3 seconds
     //   map.putIfAbsent("key2", "3000", 3, TimeUnit.SECONDS);
// ttl = 40 seconds, maxIdleTime = 10 seconds
    //    map.putIfAbsent("key2", "2000", 40, TimeUnit.SECONDS, 10, TimeUnit.MICROSECONDS);

// if object is not used anymore
      //  map.destroy();
    }

    @Test
    @DisplayName("RAtomicLong  异步执行方式 ")
    public void demo06(){
        final RAtomicLong myLong = redissonClient.getAtomicLong("myLong");
        final RFuture<Boolean> future = myLong.compareAndSetAsync(1, 1);
        future.whenComplete((res, exception) -> {
            System.out.println("complete ");
        });
// 或者
        future.thenAccept(res -> {
            // 处理返回
            System.out.println("complete 处理返回");
        }).exceptionally(exception -> {
            // 处理错误
            throw new RuntimeException(" rte");
        });

    }

    @Test
    @DisplayName("基于 Set 的 MultiMap")
    public void demo07(){
        RSetMultimap<String, String> map = redissonClient.getSetMultimap("myMultimap");
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
    }

    @Test
    @DisplayName("基于 List 的 MultiMap")
    public void demo08(){
        RListMultimap<String, String> map = redissonClient.getListMultimap("myListMultimap");
        map.put("11", "1");
        map.put("12", "2");
        map.put("13", "3");
    }
    @Test
    @DisplayName("Topic 模式 测试")
    public void demo09() throws InterruptedException {
        RTopic topic = redissonClient.getTopic("topic1");
        RTopic topic2 = redissonClient.getTopic("topic2");
        RPatternTopic topic1 = redissonClient.getPatternTopic("topic1.*");
        topic1.addListener(String.class,new PatternMessageListener() {

            @Override
            public void onMessage(CharSequence pattern, CharSequence channel, Object msg) {
                System.out.println(msg);
            }
        });

        for (int i=0 ; i<100; i++
             ) {
            topic.publish("topic1:" + i+"");
            topic2.publish("topic2:" + i +" ");
            TimeUnit.SECONDS.sleep(1);

        }

    }



}
