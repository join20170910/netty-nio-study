package com.ws.st.common.enums.command;

public enum SystemCommand implements Command{
    /**
     *  9000 登录； 9003 登出 心跳 9999 下线通知 用于多端互斥 9002
     */
    LOGIN(0x2328),
    LOGOUT(0x232b),
    PING(0x270f),
    MUTUALLOGIN(0x232a),
    ;
    private int command;
    SystemCommand(int command){
        this.command = command;
    }
    @Override
    public int getCommand() {
        return command;
    }
}
