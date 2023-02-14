package com.ws.st.tcp.publish;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.ws.st.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqMessageProducer {
    public static void sendMessage(Object message){
        Channel channel = null;
        String channelName = "";
        try{
            channel = MqFactory.getChannel(channelName);
            channel.basicPublish(channelName,"",null, JSONObject.toJSONString(message).getBytes());
        }catch (Exception e){
         log.error("发送消息出现异常: [{}]",e);
        }

    }

}
