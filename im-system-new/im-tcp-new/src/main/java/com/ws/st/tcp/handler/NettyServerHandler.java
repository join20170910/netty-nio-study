package com.ws.st.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ws.st.codec.pack.LoginPack;
import com.ws.st.codec.proto.Message;
import com.ws.st.common.enums.command.SystemCommand;
import com.ws.st.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        Integer command = msg.getMessageHeader().getCommand();
        // 登录 command
        if (command == SystemCommand.LOGIN.getCommand()){

            LoginPack loginPack = JSON.parseObject(
                    JSONObject.toJSONString(msg.getMessagePack()),
                    new TypeReference<LoginPack>(){}.getType());
            // channel 设置属性 设置  用户id
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(loginPack.getUserId());

            // 将channel 存起来
          SessionSocketHolder.put(loginPack.getUserId(),(NioSocketChannel) ctx.channel());
        }

    }
}
