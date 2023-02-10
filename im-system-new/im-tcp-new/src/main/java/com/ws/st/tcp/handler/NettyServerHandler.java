package com.ws.st.tcp.handler;

import com.ws.st.codec.proto.Message;
import com.ws.st.common.enums.command.SystemCommand;
import com.ws.st.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        Integer command = msg.getMessageHeader().getCommand();
        // 登录 command
        if (command == SystemCommand.LOGIN.getCommand()){
          // 将channel 存起来
          SessionSocketHolder.put((NioSocketChannel) ctx.channel());
        }

    }
}
