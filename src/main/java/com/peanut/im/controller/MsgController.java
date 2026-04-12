package com.peanut.im.controller;

import com.peanut.POJO.entity.Resp;
import com.peanut.annotation.CurrentUserId;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.im.pojo.dto.MessageSearchDTO;
import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.service.ChatMessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: peanut
 * @date: 2026/3/25
 * @version:1.0
 */
@RestController
@RequestMapping("/message")
@RedisLimitOnClassAnnotation
public class MsgController {

    @Autowired
    private ChatMessageService chatMessageService;
    private Logger logger = LoggerFactory.getLogger(MsgController.class);

    @GetMapping("/history")
    public Resp getMsgHistory(@Valid @RequestBody MessageSearchDTO messageSearchDTO,
                              @CurrentUserId String currentUserId) {
        List<ChatMessage> latestMessages = chatMessageService.getLatestMessages(messageSearchDTO);
        logger.info(currentUserId + " 获取消息历史记录");
        return Resp.success(latestMessages);
    }
}
