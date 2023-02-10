package com.ws.st.common.enums.command;

public enum SystemCommand implements Command{
    /**
     *  0 正常； 1 删除
     */
    LOGIN(0x2328),
    DELETE(1),
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
