package com.peanut.im.service;

import com.peanut.im.dao.DmPairsDao;
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

    @Autowired
    private DmPairsDao dmPairsDao;

    @Autowired
    private ConversationMembersService conversationMembersService;

    private static final Logger logger = LoggerFactory.getLogger(ImRedisService.class);

    public void handleMessage(ChatMessage msg) {
        if (msg.getConversationType() == ConversationType.SYSTEM) {
            return;
        }

        Long sequence = msg.getSequence(); // DB 生成的

        //每个会话的消息ID有序集合，score用sequence，value用msgId
        String msgId = msg.getMsgId();
        String convKey = "im:conv:zs:" + msg.getConversationId();
        redis.opsForZSet().add(convKey, msgId, sequence);

        //每个会话的最后一条消息ID，过期时间7天
        String lastKey = "im:conv:last:" + msg.getConversationId();
        redis.opsForValue().set(lastKey, msgId, Duration.ofDays(7));

        List<String> receiverUserIds = new java.util.ArrayList<>();
        if (msg.getConversationType() == (ConversationType.GROUP)) {
            List<String> members = conversationMembersService.getGroupMemberIds(msg.getConversationId());
            if (members != null) receiverUserIds.addAll(members);
            receiverUserIds.remove(msg.getFromUserId());
        } else if (msg.getConversationType() == (ConversationType.USER)) {
            // 单聊：对方
            String toUserId = dmPairsDao.getToUserIdByConversationId(msg.getConversationId(), msg.getFromUserId());
            if (toUserId != null && !toUserId.isBlank() && !toUserId.equals(msg.getFromUserId())) {
                receiverUserIds.add(toUserId);
            }
        }
        for (String userId : receiverUserIds) {
            // 未读计数（field为conversationId，支持多会话未读分离）
            String unreadKey = "im:unread:" + userId;
            redis.opsForHash().increment(unreadKey, msg.getConversationId(), 1L);
            redis.expire(unreadKey, Duration.ofDays(30));
        }
    }

    public boolean isMessageHandled(String clientMsgId) {
        // 生成去重Key，尝试设置，过期时间2天
        String dedupKey = "im:dedup:" + clientMsgId;
        Boolean first = redis.opsForValue().setIfAbsent(dedupKey, "1", Duration.ofDays(2));
        if (first == null || !first) {
            logger.warn("消息重复处理, 跳过消息处理: msgId={}", clientMsgId);
            return true;
        }
        return false;
    }

    public long nextListCursor(String userId) {
        Long v = redis.opsForValue().increment("im:list_cursor:" + userId);
        return v != null ? v : 1L;
    }
}
