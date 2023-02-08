package com.ws.st.codec.config;

import lombok.Data;

/**
 * 配置文件
 */
@Data
public class BootstrapConfig {
    private TcpConfig wshen;
    @Data
    public static class TcpConfig{
        private Integer tcpPort;// tcp 绑定的端口号
        private Integer webSocketPort; // webSocket 绑定的端口号
        private Integer bossThreadSize; // boss线程 默认=1
        private Integer workThreadSize; //work线程
    }
}
