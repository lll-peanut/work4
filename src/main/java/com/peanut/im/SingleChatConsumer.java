package com.peanut.im;

import com.alibaba.fastjson.JSON;
import com.peanut.im.eneity.dto.ChatEnvelope;
import com.peanut.im.mq.ImMqConfig;
import com.peanut.im.service.ChatMessageStore;
import com.peanut.im.service.ConvMetaService;
import com.peanut.utils.DateTimeFormatsUtil;
import com.peanut.ws.OnlineSessionRegistry;
import jakarta.websocket.Session;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/8
 * @version:1.0
 */
@Component
public class SingleChatConsumer {

    private final ChatMessageStore store;
    private final StringRedisTemplate redis;
    private final ConvMetaService convMetaService;

    public SingleChatConsumer(ChatMessageStore store, StringRedisTemplate redis, ConvMetaService convMetaService) {
        this.store = store;
        this.redis = redis;
        this.convMetaService = convMetaService;
    }

    @RabbitListener(queues = ImMqConfig.QUEUE_SINGLE)
    public void handle(ChatEnvelope msg) {
        if (msg == null || msg.getMsgId() == null) return;

        String dedupKey = "im:dedup:" + msg.getMsgId();
        Boolean first = redis.opsForValue().setIfAbsent(dedupKey, "1", Duration.ofDays(2));
        if (first == null || !first) return;

        // ——【会话分布式序号生成】——
        String seqKey;
        if ("dm".equals(msg.getConversationType())) {
            seqKey = "im:seq:dm:" + msg.getConversationId();
        } else if ("group".equals(msg.getConversationType())) {
            seqKey = "im:seq:group:" + msg.getConversationId();
        } else {
            // 未知会话类型，安全返回
            return;
        }
        Long seq = redis.opsForValue().increment(seqKey, 1); // Redis原子自增
        if (seq == null) return; // Redis异常，安全返回
        msg.setSequence(seq); // 写到消息对象上，后续存库、推送、前端展示

        store.saveIdempotent(msg);
        convMetaService.insert(msg);

        String msgId = msg.getMsgId();

        String payload = JSON.toJSONString(msg);
        String convKey = "im:conv:zs:" + msg.getConversationId();
        LocalDateTime serverTime = msg.getServerTime();
        redis.opsForZSet().add(convKey, msgId, DateTimeFormatsUtil.toZSetScore(serverTime));

        String lastKey = "im:conv:last:" + msg.getConversationId();
        redis.opsForValue().set(lastKey, msgId, Duration.ofDays(7));

        Set<Session> toSessions = OnlineSessionRegistry.get(msg.getToUserId());
        if (toSessions.isEmpty()) {
            String offlineKey = "im:offline:" + msg.getToUserId();
            int maxOfflineMsgCount = 100;
            redis.opsForList().rightPush(offlineKey, msgId);
            redis.opsForList().trim(offlineKey, -maxOfflineMsgCount, -1); // 只保留最新100条
            redis.expire(offlineKey, Duration.ofDays(7));

            String unreadKey = "im:unread:" + msg.getToUserId();
            redis.opsForHash().increment(unreadKey, msg.getConversationId(), 1L);
            redis.expire(unreadKey, Duration.ofDays(30));
            return;
        }

        for (Session s : toSessions) {
            try {
                s.getAsyncRemote().sendText(payload);
            } catch (Exception ignored) {}
        }
    }
}