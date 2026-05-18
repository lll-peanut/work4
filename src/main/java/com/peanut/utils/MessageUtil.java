package com.peanut.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.POJO.entity.Base;
import com.peanut.POJO.entity.Resp;
import com.peanut.expection.BusinessException;
import com.peanut.im.pojo.dto.ChatAck;
import com.peanut.im.pojo.dto.ChatMessageDTO;
import com.peanut.im.pojo.entity.ChatMessage;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/22
 * @version:1.0
 */
public class MessageUtil {

    private final static Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    public static ChatMessage parseAndCheck(String message, Session session) {
        ChatMessageDTO chatMessageDTO;
        try {
            chatMessageDTO = JSON.parseObject(message, ChatMessageDTO.class);
        } catch (Exception e) {
            logger.info(e.getMessage());
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(500, "消息格式错误，请检查！"))));
            throw new BusinessException("消息格式错误，请检查！" + e.getMessage());
        }

        if (chatMessageDTO.getContent() == null) {
            logger.info("消息格式错误，缺少必要字段: content");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.failure(500, "消息格式错误，缺少字段 content")));
            return null;
        }
        if (chatMessageDTO.getClientMsgId() == null) {
            logger.info("消息格式错误，缺少必要字段: clientMsgId");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.failure(500, "消息格式错误，缺少字段 clientMsgId")));
            return null;
        }
        if (chatMessageDTO.getMsgType() == null) {
            logger.info("消息格式错误，缺少必要字段: msgType");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.failure(500, "消息格式错误，缺少字段 msgType")));
            return null;
        }
        if (chatMessageDTO.getConversationType() == null) {
            logger.info("消息格式错误，缺少必要字段: conversationType");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.failure(500, "消息格式错误，缺少字段 conversationType")));
            return null;
        }
        if (chatMessageDTO.getConversationId() == null) {
            logger.info("消息格式错误，缺少必要字段: conversationId");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.failure(500, "消息格式错误，缺少字段 conversationId")));
            return null;
        }

        String fromUserId = (String) session.getUserProperties().get("userId");
        if (fromUserId == null) {
            logger.info("session中缺少userId");
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(500, "session中缺少userId"))));
            return null;
        }
        String msgId = IdWorker.getIdStr();
        String conversationId = chatMessageDTO.getConversationId();
        ChatMessage chatMessage = new ChatMessage(msgId, chatMessageDTO.getClientMsgId(), conversationId, fromUserId,
                LocalDateTime.now(), chatMessageDTO.getContent(), chatMessageDTO.getMsgType(),
                null, chatMessageDTO.getConversationType());
        return chatMessage;
    }

    public static ChatAck getChatAck(ChatMessage chatMessage) {
        return new ChatAck(chatMessage.getMsgId(), chatMessage.getClientMsgId());
    }
}
