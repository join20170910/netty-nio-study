package com.ws.st.tcp.handler;

import com.ws.st.common.constant.Constants;
import com.ws.st.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳处理
 */
@Slf4j
public class HearBeatHandler extends ChannelInboundHandlerAdapter {

    private Long heardBeatTime; //心跳超时时间 单位为毫秒

    public HearBeatHandler(Long heardBeatTime) {
        this.heardBeatTime = heardBeatTime;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE){
               log.info("读空闲");
            }else if (event.state() == IdleState.WRITER_IDLE){
                log.info("写空闲");
            }else if (event.state() == IdleState.ALL_IDLE){
                Long lastReadTime = (Long) ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).get();
                if(lastReadTime != null && System.currentTimeMillis()- lastReadTime > this.heardBeatTime){
                 // TODO 离线逻辑  退出后台逻辑
                    SessionSocketHolder.offlineSession((NioSocketChannel) ctx.channel());
                }
            }
        }
    }
}
