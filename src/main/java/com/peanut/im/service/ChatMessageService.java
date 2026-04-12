package com.peanut.im.service;

import com.peanut.im.pojo.dto.MessageSearchDTO;
import com.peanut.im.pojo.entity.ChatMessage;

import java.util.List;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */

public interface ChatMessageService {

    public void saveIdempotent(ChatMessage msg);

    public List<ChatMessage> getLatestMessages(MessageSearchDTO messageSearchDTO);
}
