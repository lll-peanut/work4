package com.peanut.im;

import com.alibaba.fastjson.JSON;
import com.peanut.im.eneity.ChatMessage;
import com.peanut.im.mq.ImMqConfig;
import com.peanut.im.service.ChatMessageService;
import com.peanut.im.service.ConvMetaService;
import com.peanut.im.service.ImRedisService;
import com.peanut.ws.OnlineSessionRegistry;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/8
 * @version:1.0
 */
@Component
public class SingleChatConsumer {

    private final static Logger log = LoggerFactory.getLogger(SingleChatConsumer.class);

    private final ChatMessageService store;
    private final StringRedisTemplate redis;
    private final ConvMetaService convMetaService;
    private final ImRedisService imRedisService;

    public SingleChatConsumer(ChatMessageService store, StringRedisTemplate redis,
                              ConvMetaService convMetaService, ImRedisService imRedisService) {
        this.store = store;
        this.redis = redis;
        this.convMetaService = convMetaService;
        this.imRedisService = imRedisService;
    }

    @RabbitListener(queues = ImMqConfig.QUEUE_SINGLE)
    public void handle(ChatMessage msg) {
        boolean b = imRedisService.handleMessage(msg);
        if (!b) {
            log.warn("消息被Redis去重, msgId={}", msg.getMsgId());
            return;
        }
        store.saveIdempotent(msg);
        convMetaService.insert(msg);
        String toUserId = msg.getToTargetId();
        String fromUserId = msg.getFromUserId();
        Set<Session> toUserSessions = OnlineSessionRegistry.get(toUserId);
        Set<Session> fromUserSessions = OnlineSessionRegistry.get(fromUserId);

        for (Session s : toUserSessions) {
            try {
                s.getAsyncRemote().sendText(JSON.toJSONString(msg));
            } catch (Exception e) {
                log.error(e.getMessage());
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