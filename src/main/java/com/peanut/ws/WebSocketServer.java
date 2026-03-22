package com.peanut.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.POJO.Base;
import com.peanut.POJO.Message;
import com.peanut.POJO.Resp;
import com.peanut.config.SpringContextHolder;
import com.peanut.im.eneity.ChatMessage;
import com.peanut.im.eneity.dto.ChatAck;
import com.peanut.im.eneity.dto.ChatEnvelope;
import com.peanut.im.eneity.dto.ChatMessageDTO;
import com.peanut.im.mq.ImMqConfig;
import com.peanut.im.util.ConversationIds;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
// todo 这里的value里面的值可以/chat/friend/{friendId}，这样可以直接在路径上拿到friendId，减少一次json解析
@ServerEndpoint(value = "/chat/friends", configurator = WsHandshakeConfigurator.class)
@Component
public class WebSocketServer {

    private StringRedisTemplate stringRedisTemplate;

    private RabbitTemplate rabbitTemplate;

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
        rabbitTemplate = SpringContextHolder.getBean(RabbitTemplate.class);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ChatMessageDTO chatMessageDTO;
        try {
            chatMessageDTO = JSON.parseObject(message, ChatMessageDTO.class);
        } catch (Exception e) {
            logger.info(e.getMessage());
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(500, "消息格式错误，请检查！"))));
            return;
        }
        if (chatMessageDTO.getToUserId() == null
                || chatMessageDTO.getContent() == null
                || chatMessageDTO.getClientMsgId() == null
                || chatMessageDTO.getMsgType() == null
                || chatMessageDTO.getConversationType() == null
                || chatMessageDTO.getConversationId() == null){
            logger.info("消息格式错误，缺少必要字段");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(500, "消息格式错误，缺少必要字段"))));
            return;
        }

        String fromUserId = (String) session.getUserProperties().get("userId");
        if (fromUserId == null) {
            logger.info("session中缺少userId");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(500, "session中缺少userId"))));
            return;
        }
        String msgId = IdWorker.getIdStr();
        String conversationId = chatMessageDTO.getConversationId();
        ChatMessage chatMessage = new ChatMessage(msgId, chatMessageDTO.getClientMsgId(), conversationId, fromUserId, chatMessageDTO.getToUserId(),
                LocalDateTime.now(), chatMessageDTO.getContent(), chatMessageDTO.getMsgType(),
                null, chatMessageDTO.getConversationType());
        try {
            rabbitTemplate.convertAndSend(ImMqConfig.EXCHANGE, ImMqConfig.ROUTING_SINGLE, chatMessage);
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.success(new ChatAck(msgId, chatMessageDTO.getClientMsgId()))));
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
