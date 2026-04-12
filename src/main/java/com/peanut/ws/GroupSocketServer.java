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
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: peanut
 * @date: 2026/3/16
 * @version:1.0
 */
@ServerEndpoint(value = "/chat/group/{groupId}", configurator = WsHandshakeConfigurator.class)
@Component
public class GroupSocketServer {

    private StringRedisTemplate stringRedisTemplate;

    static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private ChatProducer chatProducer;

    private static final Logger logger = LoggerFactory.getLogger(GroupSocketServer.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("groupId") String groupId) {
        if (groupId == null) {
            try {
                logger.warn("WebSocket groupId is null, closing session: {}", session.getId());
                session.close();
            } catch (IOException ignored) {
            }
            return;
        }
        GroupSessionRegistry.add(groupId, session);
        stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        chatProducer = SpringContextHolder.getBean(ChatProducer.class);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // todo 代码一样，可以抽个方法，单聊和群聊都用，减少重复代码
        ChatMessageDTO chatMessageDTO;
        try {
            chatMessageDTO = JSON.parseObject(message, ChatMessageDTO.class);
        } catch (Exception e) {
            logger.info(e.getMessage());
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(500, "消息格式错误，请检查！"))));
            return;
        }
        if (chatMessageDTO.getToTargetId() == null
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
        ChatMessage chatMessage = new ChatMessage(msgId, chatMessageDTO.getClientMsgId(), conversationId, fromUserId, chatMessageDTO.getToTargetId(),
                LocalDateTime.now(), chatMessageDTO.getContent(), chatMessageDTO.getMsgType(),
                null, chatMessageDTO.getConversationType());
        try {
            chatProducer.sendGroupChat(chatMessage);
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
        String userId = (String) session.getUserProperties().get("userId");
        OnlineSessionRegistry.remove(userId, session);
    }
}