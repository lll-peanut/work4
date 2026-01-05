package com.peanut.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.peanut.POJO.Message;
import com.peanut.config.SpringContextHolder;
import com.peanut.im.eneity.dto.ChatAck;
import com.peanut.im.eneity.dto.ChatEnvelope;
import com.peanut.im.eneity.dto.ChatSendRequest;
import com.peanut.im.mq.ImMqConfig;
import com.peanut.im.util.ConversationIds;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat/friends", configurator = WsHandshakeConfigurator.class)
@Component
public class WebSocketServer {

    private StringRedisTemplate stringRedisTemplate;

    static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private RabbitTemplate rabbitTemplate;

    @OnOpen
    public void onOpen(Session session) {
        String userId = (String) session.getUserProperties().get("userId");
        if (userId == null) {
            try {
                session.close();
            } catch (IOException ignored) {
            }
            return;
        }
        OnlineSessionRegistry.add(userId, session);
        stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        rabbitTemplate = SpringContextHolder.getBean(RabbitTemplate.class);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Message msgObj = null;
        String fromUserId = (String) session.getUserProperties().get("userId");
        if (fromUserId == null) return;
        ChatSendRequest req;

        req = JSON.parseObject(message, ChatSendRequest.class);
        if (req == null || req.getToUserId() == null || req.getContent() == null) return;
        String msgId = UUID.randomUUID().toString().replace("-", "");
        ChatEnvelope envelope = new ChatEnvelope();
        envelope.setMsgId(msgId);
        envelope.setClientMsgId(req.getClientMsgId());
        envelope.setFromUserId(fromUserId);
        envelope.setToUserId(req.getToUserId());
        envelope.setConversationId(ConversationIds.of(fromUserId, req.getToUserId()));
        envelope.setServerTime(LocalDateTime.now());
        envelope.setContent(req.getContent());
        rabbitTemplate.convertAndSend(ImMqConfig.EXCHANGE, ImMqConfig.ROUTING_SINGLE, envelope);
        try {
            session.getAsyncRemote().sendText(JSON.toJSONString(new ChatAck(msgId)));
        } catch (JSONException e) {
            e.printStackTrace(); // 输出：JSON parse error: Expected double quote at...
            return;
        }
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            if (entry.getKey().equals(msgObj.getUserId())) {
                System.out.println(msgObj.getUserId());
                entry.getValue().getAsyncRemote().sendText(msgObj.getContent());
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        String userId = (String) session.getUserProperties().get("userId");
        OnlineSessionRegistry.remove(userId, session);
        System.out.println(userId + ": onClose");
    }

    @OnError
    public void onError(Session session, Throwable t) {
        String userId = (String) session.getUserProperties().get("userId");
        OnlineSessionRegistry.remove(userId, session);
    }
}
