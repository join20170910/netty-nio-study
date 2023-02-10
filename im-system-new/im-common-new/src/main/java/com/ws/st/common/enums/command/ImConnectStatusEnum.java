package com.ws.st.common.enums.command;

public enum ImConnectStatusEnum
{
    ONLINE_STATUS(1),
    OFFLINE_STATUS(2),
    ;
    private Integer code;

    ImConnectStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
