package com.ws.st.common.model;

/**
 * 用户 session
 */

import lombok.Data;

@Data
public class UserSession {
    private String userId;
    /**
     * 应用id
     */
    private Integer appId;
    /**
     * 端标识  ios web PC  soon
     */
    private Integer clientType;
    private Integer version;
    /**
     * 连接状态 1= 在线 2= 离线
     */
    private Integer connectState;
    private Integer brokerId;
    private String brokerHost;


}
