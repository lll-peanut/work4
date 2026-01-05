package com.peanut.im.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.im.DAO.ChatMessageDao;
import com.peanut.im.eneity.dto.ChatEnvelope;
import com.peanut.im.eneity.ChatMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */
@Service
public class ChatMessageStore {

    private final ChatMessageDao chatMessageDao;

    public ChatMessageStore(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
    }

    public void saveIdempotent(ChatEnvelope msg) {
        System.out.println("666");
        ChatMessage e = new ChatMessage();
        e.setId(IdWorker.getIdStr());
        e.setClientMsgId(msg.getClientMsgId());
        e.setConversationId(msg.getConversationId());
        e.setFromUserId(msg.getFromUserId());
        e.setToUserId(msg.getToUserId());
        e.setServerTime(msg.getServerTime());
        e.setContent(msg.getContent());
        e.setMsgType(1);
        try {
            System.out.println("333");
            boolean b = chatMessageDao.saveMessage(e);System.out.println("333");
            if (!b) {
                System.out.println("777");
                throw new RuntimeException("Failed to save message");
            } else {
                System.out.println("Message saved successfully: " + msg.getMsgId());
            }
        } catch (DataIntegrityViolationException dup) {
            System.out.println("Duplicate/constraint violation, msgId=" + msg.getMsgId());
            dup.printStackTrace();
        }
    }
}
