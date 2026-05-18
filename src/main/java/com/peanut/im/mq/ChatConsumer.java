package com.peanut.im.mq;

import com.alibaba.fastjson.JSON;
import com.peanut.im.dao.ConversationMemberDao;
import com.peanut.im.dao.DmPairsDao;
import com.peanut.im.enumPackage.ConversationType;
import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.service.ChatMessageService;
import com.peanut.im.service.ConvMetaService;
import com.peanut.im.service.ConversationMembersService;
import com.peanut.im.service.ImRedisService;
import com.peanut.ws.GroupSessionRegistry;
import com.peanut.ws.OnlineSessionRegistry;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/8
 * @version:1.0
 */
@Component
public class ChatConsumer {

    private final static Logger log = LoggerFactory.getLogger(ChatConsumer.class);

    private final ChatMessageService store;
    private final StringRedisTemplate redis;
    private final ConvMetaService convMetaService;
    private final ImRedisService imRedisService;
    private final DmPairsDao dmPairsDao;
    private final ConversationMembersService conversationMembersService;

    public ChatConsumer(ChatMessageService store, StringRedisTemplate redis,
                        ConvMetaService convMetaService, ImRedisService imRedisService,
                        DmPairsDao dmPairsDao, ConversationMembersService conversationMembersService) {
        this.store = store;
        this.redis = redis;
        this.convMetaService = convMetaService;
        this.imRedisService = imRedisService;
        this.dmPairsDao = dmPairsDao;
        this.conversationMembersService = conversationMembersService;
    }

    @RabbitListener(queues = "#{T(com.peanut.im.mq.ImMqConfig).getAllSingleChatQueues()}")
    public void handle(ChatMessage msg) {
        List<String> list = new ArrayList<>();
        if (msg.getConversationType() == ConversationType.USER) {
            list.add(dmPairsDao.getToUserIdByConversationId(msg.getConversationId(),  msg.getFromUserId()));
        } else if (msg.getConversationType() == ConversationType.GROUP) {
            list.addAll(conversationMembersService.getGroupMemberIds(msg.getConversationId()));
        }
        String fromUserId = msg.getFromUserId();
        Set<Session> fromUserSessions = OnlineSessionRegistry.get(fromUserId);

        for (String toUserId : list) {
            Set<Session> sessions = OnlineSessionRegistry.get(toUserId);
            for (Session s : sessions) {
                try {
                    s.getAsyncRemote().sendText(JSON.toJSONString(msg));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        for (Session s : fromUserSessions) {
            try {
                s.getAsyncRemote().sendText(JSON.toJSONString(msg));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}