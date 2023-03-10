package com.lld.im.service.message.service;

import com.lld.im.codec.pack.message.ChatMessageAck;
import com.lld.im.codec.pack.message.MessageReciveServerAckPack;
import com.lld.im.codec.proto.Message;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.ConversationTypeEnum;
import com.lld.im.common.enums.command.MessageCommand;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.common.model.message.MessageContent;
import com.lld.im.common.model.message.OfflineMessageContent;
import com.lld.im.service.message.model.req.SendMessageReq;
import com.lld.im.service.message.model.resp.SendMessageResp;
import com.lld.im.service.seq.RedisSeq;
import com.lld.im.service.utils.ConversationIdGenerate;
import com.lld.im.service.utils.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Service
public class P2PMessageService {

    private static Logger logger = LoggerFactory.getLogger(P2PMessageService.class);

    @Autowired
    CheckSendMessageService checkSendMessageService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    MessageStoreService messageStoreService;

    @Autowired
    RedisSeq redisSeq;


    private final ThreadPoolExecutor threadPoolExecutor;

    {
        final AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("message-process-thread-" + num.getAndIncrement());
                return thread;
            }
        });
    }

    //??????
    //????????????
    //1.mysql
    //2.redis
    //????????????
    //list


    //????????????

    //????????????????????????
    //messageKey
    //redis 1 2 3
    public void process(MessageContent messageContent){

        String fromId = messageContent.getFromId();
        String toId = messageContent.getToId();
        Integer appId = messageContent.getAppId();

        MessageContent messageFromMessageIdCache = messageStoreService.getMessageFromMessageIdCache
                (messageContent.getAppId(), messageContent.getMessageId(),MessageContent.class);
        if (messageFromMessageIdCache != null){
            threadPoolExecutor.execute(() ->{
                ack(messageContent,ResponseVO.successResponse());
                //2.???????????????????????????
                syncToSender(messageFromMessageIdCache,messageFromMessageIdCache);
                //3.???????????????????????????
                List<ClientInfo> clientInfos = dispatchMessage(messageFromMessageIdCache);
                if(clientInfos.isEmpty()){
                    //?????????????????????????????????????????????????????????????????????
                    reciverAck(messageFromMessageIdCache);
                }
            });
            return;
        }

        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":"
                + Constants.SeqConstants.Message+ ":" + ConversationIdGenerate.generateP2PId(
                messageContent.getFromId(),messageContent.getToId()
        ));
        messageContent.setMessageSequence(seq);

        //????????????
        //??????????????????????????? ???????????????
        //????????????????????????????????????
//        ResponseVO responseVO = imServerPermissionCheck(fromId, toId, appId);
//        if(responseVO.isOk()){
            threadPoolExecutor.execute(() ->{
                //appId + Seq + (from + to) groupId
                messageStoreService.storeP2PMessage(messageContent);

                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyProperties(messageContent,offlineMessageContent);
                offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
                messageStoreService.storeOfflineMessage(offlineMessageContent);

                //????????????
                //1.???ack???????????????
                ack(messageContent,ResponseVO.successResponse());
                //2.???????????????????????????
                syncToSender(messageContent,messageContent);
                //3.???????????????????????????
                List<ClientInfo> clientInfos = dispatchMessage(messageContent);

                messageStoreService.setMessageFromMessageIdCache(messageContent.getAppId(),
                        messageContent.getMessageId(),messageContent);
                if(clientInfos.isEmpty()){
                    //?????????????????????????????????????????????????????????????????????
                    reciverAck(messageContent);
                }
            });
//        }else{
//            //????????????????????????
//            //ack
//            ack(messageContent,responseVO);
//        }
    }

    private List<ClientInfo> dispatchMessage(MessageContent messageContent){
        List<ClientInfo> clientInfos = messageProducer.sendToUser(messageContent.getToId(), MessageCommand.MSG_P2P,
                messageContent, messageContent.getAppId());
        return clientInfos;
    }

    private void ack(MessageContent messageContent,ResponseVO responseVO){
        logger.info("msg ack,msgId={},checkResut{}",messageContent.getMessageId(),responseVO.getCode());

        ChatMessageAck chatMessageAck = new
                ChatMessageAck(messageContent.getMessageId(),messageContent.getMessageSequence());
        responseVO.setData(chatMessageAck);
        //?????????
        messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_ACK,
                responseVO,messageContent
                );
    }

    public void reciverAck(MessageContent messageContent){
        MessageReciveServerAckPack pack = new MessageReciveServerAckPack();
        pack.setFromId(messageContent.getToId());
        pack.setToId(messageContent.getFromId());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setMessageSequence(messageContent.getMessageSequence());
        pack.setServerSend(true);
        messageProducer.sendToUser(messageContent.getFromId(),MessageCommand.MSG_RECIVE_ACK,
                pack,new ClientInfo(messageContent.getAppId(),messageContent.getClientType()
                ,messageContent.getImei()));
    }

    private void syncToSender(MessageContent messageContent, ClientInfo clientInfo){
            messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                    MessageCommand.MSG_P2P,messageContent,messageContent);
    }

    public ResponseVO imServerPermissionCheck(String fromId,String toId,
                                               Integer appId){
        ResponseVO responseVO = checkSendMessageService.checkSenderForvidAndMute(fromId, appId);
        if(!responseVO.isOk()){
            return responseVO;
        }
        responseVO = checkSendMessageService.checkFriendShip(fromId, toId, appId);
        return responseVO;
    }

    public SendMessageResp send(SendMessageReq req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        BeanUtils.copyProperties(req,message);
        //????????????
        messageStoreService.storeP2PMessage(message);
        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());

        //2.???????????????????????????
        syncToSender(message,message);
        //3.???????????????????????????
        dispatchMessage(message);
        return sendMessageResp;
    }
}
