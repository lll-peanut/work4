package com.peanut.im.service.Impl;

import com.peanut.im.dao.ChatMessageDao;
import com.peanut.im.enumPackage.MessageType;
import com.peanut.im.pojo.dto.MessageSearchDTO;
import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Value("${im.history.size:20}")
    private Long size;

    private final ChatMessageDao chatMessageDao;

    private final StringRedisTemplate stringRedisTemplate;

    public ChatMessageServiceImpl(ChatMessageDao chatMessageDao, StringRedisTemplate stringRedisTemplate) {
        this.chatMessageDao = chatMessageDao;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void saveIdempotent(ChatMessage msg) {
        try {
            boolean b = chatMessageDao.saveMessage(msg);
            if (!b) {
                throw new RuntimeException("Failed to save message");
            }
        } catch (DataIntegrityViolationException dup) {
            System.out.println("Duplicate/constraint violation, msgId=" + msg.getMsgId());
            dup.printStackTrace();
        }
    }

    @Override
    public List<ChatMessage> getLatestMessages(MessageSearchDTO searchDTO) {
        String conversationId = searchDTO.getConversationId();
        String redisKey = "im:conv:zs:" + conversationId;
        if (searchDTO.getFromDate() == null && searchDTO.getToDate() == null) {
            // 1. 从Redis zset倒序获取最新count条
            Set<String> redisMsgIds = stringRedisTemplate.opsForZSet()
                    .reverseRange(redisKey, 0, size - 1);
            List<ChatMessage> messageList = new ArrayList<>();

            if (redisMsgIds != null && !redisMsgIds.isEmpty() && redisMsgIds.size() == size) {
                // 2. 根据ID列表从数据库批量拉消息正文（假设zset只存 msgId，实际存json可反序列）
                // 2.1 可扩展：可以把消息正文直接存 zset，减少一次SQL
                messageList = chatMessageDao.findByMsgIdDesc(redisMsgIds, size);
            }
            return messageList.reversed();
        }

        // 3. 如果Redis无数据，则查数据库
        return chatMessageDao
                .findHistory(searchDTO, size);
    }
}
