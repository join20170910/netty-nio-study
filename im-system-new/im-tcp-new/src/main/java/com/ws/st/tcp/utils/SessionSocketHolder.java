package com.ws.st.tcp.utils;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NioSocketChannel 容器
 */
public class SessionSocketHolder {
    private static final Map<String, NioSocketChannel> CHANENELS = new ConcurrentHashMap<>();

    public static void put(NioSocketChannel channel){
        CHANENELS.put(channel.remoteAddress().getHostName(),channel);
    }

    public static NioSocketChannel get(NioSocketChannel channel){
        return CHANENELS.get(channel.remoteAddress().getHostName());
    }
}
