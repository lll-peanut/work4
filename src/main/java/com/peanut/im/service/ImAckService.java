package com.peanut.im.service;

import com.alibaba.fastjson.JSON;
import com.peanut.im.dao.DmPairsDao;
import com.peanut.im.enumPackage.ImWsEnvelopeType;
import com.peanut.im.pojo.dto.ImWsEnvelope;
import com.peanut.ws.OnlineSessionRegistry;
import jakarta.websocket.Session;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/5/19
 * @version:1.0
 */
@Service
public class ImAckService {

    private final StringRedisTemplate redis;
    private final DmPairsDao dmPairsDao;
    public ImAckService(StringRedisTemplate redis, DmPairsDao dmPairsDao) {
        this.redis = redis;
        this.dmPairsDao = dmPairsDao;
    }

    public void onDelivered(String toUserId, String conversationId, long maxSeq) {
        // 记录接收端已送达的最大序号（用于补拉/对账）
        String key = "im:delivered:" + toUserId + ":" + conversationId;
        redis.opsForValue().set(key, String.valueOf(maxSeq), Duration.ofDays(30));

        // 通知发送端（单聊：toUserId 的对端是 fromUserId，需要查 pair；群聊：通知每个发送者？通常只需要通知本条消息发送者）
        // 最小实现：先只做单聊：通知对端（发送者）“你发到我这里已送达至 maxSeq”
        String fromUserId = dmPairsDao.getToUserIdByConversationId(conversationId, toUserId); // 需要你实现一个能反查对端的方法

        if (fromUserId == null) return;

        ImWsEnvelope<Object> notice = new ImWsEnvelope<>();
        notice.setType(ImWsEnvelopeType.DELIVERED_NOTICE);
        notice.setConversationId(conversationId);
        notice.setMaxSeq(maxSeq);

        Set<Session> fromSessions = OnlineSessionRegistry.get(fromUserId);
        for (Session s : fromSessions) {
            try { s.getAsyncRemote().sendText(JSON.toJSONString(notice)); } catch (Exception ignored) {}
        }
    }
}