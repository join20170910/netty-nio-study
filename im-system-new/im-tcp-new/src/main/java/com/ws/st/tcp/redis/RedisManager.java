package com.ws.st.tcp.redis;

import com.ws.st.codec.config.BootstrapConfig;
import org.redisson.api.RedissonClient;

/**
 *  管理类
 */
public class RedisManager {

    private static RedissonClient redissonClient;

    private static Integer loginModel;

    public static void init(BootstrapConfig config){

        loginModel = config.getWshen().getLoginModel();
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getWshen().getRedis());

    }

    public static RedissonClient getRedissonClient(){
        return redissonClient;
    }

}
