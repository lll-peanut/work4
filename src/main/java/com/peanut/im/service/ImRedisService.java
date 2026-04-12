package com.peanut.im.service;

import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.enumPackage.ConversationType;
import com.peanut.ws.OnlineSessionRegistry;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
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

    @Autowired
    private GroupService groupService;

    private static final Logger logger = LoggerFactory.getLogger(ImRedisService.class);

    public boolean handleMessage(ChatMessage msg) {
        if (msg.getConversationType() == ConversationType.SYSTEM) {
            // TODO: 系统消息推送/日志/单独存储
            return true;
        }

        // 生成去重Key，尝试设置，过期时间2天
        String dedupKey = "im:dedup:" + msg.getClientMsgId();
        Boolean first = redis.opsForValue().setIfAbsent(dedupKey, "1", Duration.ofDays(2));
        if (first == null || !first) {
            logger.warn("Redis去重setIfAbsent异常, 跳过消息处理: msgId={}", msg.getMsgId());
            return false;
        }
        // todo seq用Long，之后加个归档 redis掉了要从mysql里补数据，超过21亿就有问题了
        String seqKey = "im:seq:" + msg.getConversationId();
        Long seq = redis.opsForValue().increment(seqKey);
        Long sequence = seq != null ? seq : 1L;
        msg.setSequence(sequence);


        //每个会话的消息ID有序集合，score用sequence，value用msgId
        String msgId = msg.getMsgId();
        String convKey = "im:conv:zs:" + msg.getConversationId();
        redis.opsForZSet().add(convKey, msgId, sequence);

        //每个会话的最后一条消息ID，过期时间7天
        String lastKey = "im:conv:last:" + msg.getConversationId();
        redis.opsForValue().set(lastKey, msgId, Duration.ofDays(7));

        Set<String> receiverUserIds = null;
        if (msg.getConversationType() == (ConversationType.GROUP)) {
            // 群聊：全体成员列表
            receiverUserIds = groupService.getGroupMembersUserIds(msg.getConversationId());
        } else if (msg.getConversationType() == (ConversationType.USER)) {
            // 单聊：对方
            receiverUserIds = Collections.singleton(msg.getToTargetId());
        }
        for (String userId : receiverUserIds) {
            // 未读计数（field为conversationId，支持多会话未读分离）
            String unreadKey = "im:unread:" + userId;
            redis.opsForHash().increment(unreadKey, msg.getConversationId(), 1L);
            redis.expire(unreadKey, Duration.ofDays(30));
        }
        return true;
    }
}
