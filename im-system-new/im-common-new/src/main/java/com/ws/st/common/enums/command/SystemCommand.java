package com.ws.st.common.enums.command;

public enum SystemCommand implements Command{
    /**
     *  9000 登录； 9003 登出
     */
    LOGIN(0x2328),
    LOGOUT(0x232b),
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
