package com.ws.st.tcp.utils;

import com.ws.st.common.model.UserClientDto;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NioSocketChannel 容器
 */
public class SessionSocketHolder {
    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(String userId,Integer clientType,Integer appId, NioSocketChannel channel){
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        CHANNELS.put(userClientDto,channel);
    }

    public static NioSocketChannel get(String userId,Integer clientType,Integer appId){
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        return CHANNELS.get(userClientDto);
    }

    /**
     * 删除
     * @param userId
     * @param clientType
     * @param appId
     */
    public static void remove(String userId,Integer clientType,Integer appId){
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        CHANNELS.remove(userClientDto);
    }

    /**
     * 删除 channel
     * @param channel
     */
    public static void remove(NioSocketChannel channel){
        CHANNELS.entrySet().stream().
                filter(entity -> entity.getValue() == channel)
                .forEach(entry ->CHANNELS.remove(entry.getKey()));
    }
}
