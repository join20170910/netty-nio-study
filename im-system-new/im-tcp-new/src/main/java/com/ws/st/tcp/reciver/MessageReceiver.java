package com.ws.st.tcp.reciver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.ws.st.common.constant.Constants;
import com.ws.st.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MessageReceiver {
    private static void startReceiverMessage(){
   try{
       Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MessageService2Im);
       channel.queueDeclare(Constants.RabbitConstants.MessageService2Im,
               true,true,false,null);
       channel.queueBind(Constants.RabbitConstants.MessageService2Im,Constants.RabbitConstants.MessageService2Im,null);
       channel.basicConsume(Constants.RabbitConstants.MessageService2Im,false,
               new DefaultConsumer(channel){
                   @Override
                   public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                       //TODO 处理消息服务发来的消息
                       String msgStr = new String(body);
                       log.info("接收到消息内容：[{}]",msgStr);
                   }
               }

       );
   }catch (Exception e){

   }
    }
    public static void init(){
        startReceiverMessage();
    }
}
