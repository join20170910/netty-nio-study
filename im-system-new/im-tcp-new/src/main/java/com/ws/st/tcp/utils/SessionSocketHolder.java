package com.ws.st.tcp.utils;

import com.alibaba.fastjson.JSONObject;
import com.ws.st.common.constant.Constants;
import com.ws.st.common.enums.command.ImConnectStatusEnum;
import com.ws.st.common.model.UserClientDto;
import com.ws.st.common.model.UserSession;
import com.ws.st.tcp.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * NioSocketChannel 容器
 */
public class SessionSocketHolder {
    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(String userId,Integer clientType,Integer appId, NioSocketChannel channel,String imei){
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        userClientDto.setImei(imei);
        CHANNELS.put(userClientDto,channel);
    }

    public static List<NioSocketChannel> get(Integer appId, String userId){
        Set<UserClientDto> channelInfos = CHANNELS.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();
        channelInfos.stream().filter(x -> x.getAppId().equals(appId)
                && userId.equals(x.getUserId())).map(channel ->
                channels.add(CHANNELS.get(channels))
        );

        /*channelInfos.forEach(channel ->{
            if(channel.getAppId().equals(appId) && userId.equals(channel.getUserId())){
                channels.add(CHANNELS.get(channel));
            }
        });*/

        return channels;
    }

    public static NioSocketChannel get(String userId,Integer clientType,Integer appId,String imei){
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        userClientDto.setImei(imei);
        return CHANNELS.get(userClientDto);
    }

    /**
     * 删除
     * @param userId
     * @param clientType
     * @param appId
     */
    public static void remove(String userId,Integer clientType,Integer appId,String imei){
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        userClientDto.setImei(imei);
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

    /**
     *  删除 session 关闭链接
     * @param channel
     */
    public static void removeUserSession(NioSocketChannel channel){
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(Constants.Imei)).get();
        SessionSocketHolder.remove(userId,clientType,appId,imei);
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisConstants.UserSessionConstants
                + userId + imei);
        map.remove(clientType);
        channel.close();
    }

    public static void offlineSession(NioSocketChannel channel){
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(Constants.Imei)).get();
        SessionSocketHolder.remove(userId,clientType,appId,imei);
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisConstants.UserSessionConstants
                + userId + imei);
        String clientTypeStr = String.valueOf(clientType);
        String sessionStr = (String) map.get(clientTypeStr + ":" + imei);
        if (StringUtils.isBlank(sessionStr)){
            UserSession userSession = JSONObject.parseObject(sessionStr, UserSession.class);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientTypeStr + ":" + imei ,JSONObject.toJSONString(userSession));
        }
        map.remove(clientType + ":" + imei);
        channel.close();
    }
}


