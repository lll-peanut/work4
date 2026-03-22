package com.peanut.im.service;

import com.peanut.im.eneity.ChatMessage;
import org.springframework.stereotype.Service;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */

public interface ChatMessageService {

    public void saveIdempotent(ChatMessage msg);
}
