package com.ws.st.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ws.st.codec.pack.LoginPack;
import com.ws.st.codec.proto.Message;
import com.ws.st.common.constant.Constants;
import com.ws.st.common.enums.command.ImConnectStatusEnum;
import com.ws.st.common.enums.command.SystemCommand;
import com.ws.st.common.model.UserSession;
import com.ws.st.tcp.redis.RedisManager;
import com.ws.st.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

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
            ctx.channel().attr(AttributeKey.valueOf(Constants.UserId)).set(loginPack.getUserId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.AppId)).set(msg.getMessageHeader().getAppId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.ClientType)).set(msg.getMessageHeader().getClientType());

            // 将channel 存起来
          SessionSocketHolder
                  .put(loginPack.getUserId(),
                          msg.getMessageHeader().getClientType(),
                          msg.getMessageHeader().getAppId(),(NioSocketChannel) ctx.channel());
          // Redis map 存放 用户 session 信息
            UserSession userSession = new UserSession();
            userSession.setAppId(msg.getMessageHeader().getAppId());
             userSession.setClientType(msg.getMessageHeader().getClientType());
             userSession.setUserId(loginPack.getUserId());
             userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
             // TODO 启动 初始化 redssion 客户端
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(userSession.getAppId() + Constants.RedisConstants.UserSessionConstants + loginPack.getUserId());
            map.put(String.valueOf(msg.getMessageHeader().getClientType()),JSONObject.toJSONString(userSession));
        } else if (command == SystemCommand.LOGOUT.getCommand()){
            // 登出
            // TODO 删除 session redis 信息
           SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
        }else if (command == SystemCommand.PING.getCommand()){
            // 设置当前时间
            ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).set(System.currentTimeMillis());

        }

    }

    // IdleStateHandler 读写 超时 会触发下一个handler 的userEventTriggered（）方法
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
         // 一般 写一个 单独的 handler
    }
}
