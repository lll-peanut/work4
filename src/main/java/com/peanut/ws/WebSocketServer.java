package com.peanut.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.POJO.entity.Base;
import com.peanut.POJO.entity.Resp;
import com.peanut.config.SpringContextHolder;
import com.peanut.im.enumPackage.ImWsEnvelopeType;
import com.peanut.im.pojo.dto.ImWsEnvelope;
import com.peanut.im.pojo.dto.SendMessageResultDTO;
import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.pojo.dto.ChatAck;
import com.peanut.im.pojo.dto.ChatMessageDTO;
import com.peanut.im.mq.ChatProducer;
import com.peanut.im.service.ConversationService;
import com.peanut.im.service.ImAckService;
import com.peanut.im.service.ImSyncService;
import com.peanut.utils.CheckUtil;
import com.peanut.utils.IdUtil;
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
    private ImAckService ackService;

    private ImSyncService syncService;

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
        conversationService = SpringContextHolder.getBean(ConversationService.class);
        ackService = SpringContextHolder.getBean(ImAckService.class);
        syncService = SpringContextHolder.getBean(ImSyncService.class);
    }

    @OnMessage
    public void onMessage(String raw, Session session) {

        String userId = (String) session.getUserProperties().get("userId");
        if (userId == null) {
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(401, "未登录或登录过期"))));
            return;
        }

        ImWsEnvelope<?> env;
        try {
            env = JSON.parseObject(raw, ImWsEnvelope.class);
        } catch (Exception e) {
            logger.info(e.toString());
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(400, "bad envelope"))));
            return;
        }

        if (CheckUtil.checkImWsEnvelope(env) == 0) {
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(400, "缺少必要字段"))));
            return;
        }
        switch (env.getType()) {
            case ImWsEnvelopeType.MSG -> handleMsg(raw, userId, session);
            case ImWsEnvelopeType.DELIVER_ACK -> handleDeliverAck(env, userId);
            case ImWsEnvelopeType.SYNC_REQ -> handleSync(env, userId, session);
            default -> session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(400, "unknown type"))));
        }
    }

    private void handleMsg(String raw, String fromUserId, Session session) {
        ImWsEnvelope<ChatMessageDTO> env =
                JSON.parseObject(raw, new TypeReference<ImWsEnvelope<ChatMessageDTO>>() {
                });

        ChatMessageDTO dto = env.getPayload();
        if (dto == null) {
            session.getAsyncRemote().sendText(JSON.toJSONString(Resp.fail(new Base(400, "missing payload"))));
            return;
        }
        ChatMessage msg = new ChatMessage(IdUtil.getId(), dto.getClientMsgId(), dto.getConversationId(), fromUserId,
                LocalDateTime.now(), dto.getContent(), dto.getMsgType(),
                null, dto.getConversationType());
        SendMessageResultDTO result = conversationService.sendMessage(msg);
        if (result == null) {
            return;
        }

        ImWsEnvelope<Object> ack = new ImWsEnvelope<>(ImWsEnvelopeType.SERVER_ACK, result.getConversationId(), dto.getConversationType(), result.getMsgId(), dto.getClientMsgId(), result.getSeq(), null, null, null);
        session.getAsyncRemote().sendText(JSON.toJSONString(ack));
    }

    private void handleDeliverAck(ImWsEnvelope<?> env, String toUserId) {
        // 接收端告知：该会话我已经收到到 maxSeq
        ackService.onDelivered(toUserId, env.getConversationId(), env.getMaxSeq());
    }

    private void handleSync(ImWsEnvelope<?> env, String userId, Session session) {
        if (env.getConversationId() == null || env.getFromSeqExclusive() == null) return;
        ImWsEnvelope<Object> resp = syncService.sync(userId, env.getConversationId(), env.getFromSeqExclusive());
        session.getAsyncRemote().sendText(JSON.toJSONString(resp));
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
