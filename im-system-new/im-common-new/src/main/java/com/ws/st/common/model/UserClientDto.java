package com.ws.st.common.model;
import lombok.Data;

/**
 * CHANNELS 对象  key：UserClientDto value channel
 * netty  channel 与用户的关系对象
 */
@Data
public class UserClientDto{
    private Integer appId;
    private Integer clientType;
    private String userId;
    private String imei;
}
