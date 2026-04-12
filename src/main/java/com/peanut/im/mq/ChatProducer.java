package com.peanut.im.mq;

import com.peanut.im.pojo.entity.ChatMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: peanut
 * @date: 2026/3/22
 * @version:1.0
 */
@Component
public class ChatProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSingleChat(ChatMessage chatMessage) {
        String singleChatRoutingKey = ImMqConfig.getSingleChatRoutingKey(chatMessage.getFromUserId());
        rabbitTemplate.convertAndSend(ImMqConfig.EXCHANGE, singleChatRoutingKey, chatMessage);
    }

    public void sendGroupChat(ChatMessage chatMessage) {
        String groupChatRoutingKey = ImMqConfig.getGroupChatRoutingKey(chatMessage.getConversationId());
        rabbitTemplate.convertAndSend(ImMqConfig.GROUP_EXCHANGE, groupChatRoutingKey, chatMessage);
    }
}