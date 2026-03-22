package com.peanut.im.service;

import com.alibaba.fastjson.JSON;
import com.peanut.im.eneity.ChatMessage;
import com.peanut.utils.DateTimeFormatsUtil;
import com.peanut.ws.OnlineSessionRegistry;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/21
 * @version:1.0
 */
@Service
public class ImRedisService {

    @Value("${im.offline.maxCount:100}")
    private Long offlineMaxCount;

    @Autowired
    private StringRedisTemplate redis;

    private static final Logger logger = LoggerFactory.getLogger(ImRedisService.class);

    public boolean handleMessage(ChatMessage msg) {

        // 生成去重Key，尝试设置，过期时间2天
        String dedupKey = "im:dedup:" + msg.getClientMsgId();
        Boolean first = redis.opsForValue().setIfAbsent(dedupKey, "1", Duration.ofDays(2));
        if (first == null || !first) {
            logger.warn("Redis去重setIfAbsent异常, 跳过消息处理: msgId={}", msg.getMsgId());
            return false;
        }

        String seqKey = "im:seq:" + msg.getConversationId();;
        Long seq = redis.opsForValue().increment(seqKey);
        Long sequence = seq != null ? seq.intValue() : 1L;
        msg.setSequence(sequence);

        //每个会话的消息ID有序集合，score用sequence，value用msgId
        String msgId = msg.getMsgId();
        String convKey = "im:conv:zs:" + msg.getConversationId();
        redis.opsForZSet().add(convKey, msgId, sequence);

        //每个会话的最后一条消息ID，过期时间7天
        String lastKey = "im:conv:last:" + msg.getConversationId();
        redis.opsForValue().set(lastKey, msgId, Duration.ofDays(7));
        String toTargetId = msg.getToTargetId();

        Set<Session> toSessions = OnlineSessionRegistry.get(toTargetId);
        if (toSessions.isEmpty()) {
            // 离线消息列表，右推入消息ID，保留最新100条，过期时间7天
            String offlineKey = "im:offline:" + toTargetId;
            redis.opsForList().rightPush(offlineKey, msgId);
            redis.opsForList().trim(offlineKey, -offlineMaxCount, -1); // 只保留最新100条
            redis.expire(offlineKey, Duration.ofDays(7));
            // 增加未读消息计数，Hash结构，field是conversationId，value是未读数，过期时间30天
            String unreadKey = "im:unread:" + toTargetId;
            redis.opsForHash().increment(unreadKey, msg.getConversationId(), 1L);
            redis.expire(unreadKey, Duration.ofDays(30));
        }
        return true;
    }

    public boolean handleGroupMessage(ChatMessage msg) {

        // 生成去重Key，尝试设置，过期时间2天
        String dedupKey = "im:dedup:" + msg.getClientMsgId();
        Boolean first = redis.opsForValue().setIfAbsent(dedupKey, "1", Duration.ofDays(2));
        if (first == null || !first) {
            logger.warn("Redis去重setIfAbsent异常, 跳过消息处理: msgId={}", msg.getMsgId());
            return false;
        }

        String seqKey = "im:seq:" + msg.getConversationId();;
        Long seq = redis.opsForValue().increment(seqKey);
        Long sequence = seq != null ? seq.intValue() : 1L;
        msg.setSequence(sequence);

        //每个会话的消息ID有序集合，score用sequence，value用msgId
        String msgId = msg.getMsgId();
        String convKey = "im:conv:zs:" + msg.getConversationId();
        redis.opsForZSet().add(convKey, msgId, sequence);

        //每个会话的最后一条消息ID，过期时间7天
        String lastKey = "im:conv:last:" + msg.getConversationId();
        redis.opsForValue().set(lastKey, msgId, Duration.ofDays(7));
        String toTargetId = msg.getToTargetId();

        Set<Session> toSessions = OnlineSessionRegistry.get(toTargetId);
        if (toSessions.isEmpty()) {
            // 离线消息列表，右推入消息ID，保留最新100条，过期时间7天
            String offlineKey = "im:offline:" + toTargetId;
            redis.opsForList().rightPush(offlineKey, msgId);
            redis.opsForList().trim(offlineKey, -offlineMaxCount, -1); // 只保留最新100条
            redis.expire(offlineKey, Duration.ofDays(7));
            // 增加未读消息计数，Hash结构，field是conversationId，value是未读数，过期时间30天
            String unreadKey = "im:unread:" + toTargetId;
            redis.opsForHash().increment(unreadKey, msg.getConversationId(), 1L);
            redis.expire(unreadKey, Duration.ofDays(30));
        }
        return true;
    }
}
