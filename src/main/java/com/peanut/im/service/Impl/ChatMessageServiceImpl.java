package com.peanut.im.service.Impl;

import com.peanut.im.dao.ChatMessageDao;
import com.peanut.im.eneity.ChatMessage;
import com.peanut.im.service.ChatMessageService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageDao chatMessageDao;

    public ChatMessageServiceImpl(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
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
}
