package com.peanut.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.POJO.entity.Base;
import com.peanut.POJO.entity.Resp;
import com.peanut.config.SpringContextHolder;
import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.pojo.dto.ChatAck;
import com.peanut.im.pojo.dto.ChatMessageDTO;
import com.peanut.im.mq.ChatProducer;
import com.peanut.im.service.ConversationService;
import com.peanut.utils.MessageUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@ServerEndpoint(value = "/chat", configurator = WsHandshakeConfigurator.class)
@Component
public class WebSocketServer {

    private StringRedisTemplate stringRedisTemplate;

    private ChatProducer chatProducer;

    private ConversationService conversationService;

    private final static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

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
        chatProducer = SpringContextHolder.getBean(ChatProducer.class);
        conversationService = SpringContextHolder.getBean(ConversationService.class);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ChatMessage chatMessage = MessageUtil.parseAndCheck(message, session);
        try {
            if (chatMessage == null) {
                throw new JSONException("消息格式错误，缺少必要字段");
            }
            conversationService.sendMessage(chatMessage);
            chatProducer.sendSingleChat(chatMessage);
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.success(MessageUtil.getChatAck(chatMessage))));
        } catch (JSONException e) {
            logger.error("消息发送失败:", e);
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(500, "session中缺少userId"))));
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
        logger.error(t.getMessage(), t);
        String userId = (String) session.getUserProperties().get("userId");
        OnlineSessionRegistry.remove(userId, session);
    }
}
