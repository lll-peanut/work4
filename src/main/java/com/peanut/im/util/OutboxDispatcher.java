package com.peanut.im.util;

import com.alibaba.fastjson.JSON;
import com.peanut.im.dao.ImOutboxDao;
import com.peanut.im.mq.ChatProducer;
import com.peanut.im.pojo.dto.ImWsEnvelope;
import com.peanut.im.pojo.entity.ChatMessage;
import com.peanut.im.pojo.entity.ImOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: peanut
 * @date: 2026/5/19
 * @version:1.0
 */
@Component
public class OutboxDispatcher {

    private static final Logger log = LoggerFactory.getLogger(OutboxDispatcher.class);

    private final ImOutboxDao dao;
    private final ChatProducer producer;

    public OutboxDispatcher(ImOutboxDao dao, ChatProducer producer) {
        this.dao = dao;
        this.producer = producer;
    }

    @Scheduled(fixedDelayString = "${im.outbox.dispatch.delayMs:200}")
    public void dispatch() {
        List<ImOutbox> due = dao.selectDue(200);
        for (ImOutbox row : due) {
            try {
                ChatMessage msg = JSON.parseObject(row.getPayloadJson(), ChatMessage.class);
                producer.sendChatMessage(msg);

                dao.markSent(row.getId());
            } catch (Exception e) {
                log.warn("outbox dispatch failed id={}, msgId={}, err={}", row.getId(), row.getMsgId(), e.getMessage());
                // 指数退避
                int backoffSec = Math.min(60, (int) Math.pow(2, Math.min(6, row.getRetryCount() == null ? 0 : row.getRetryCount())));
                dao.scheduleRetry(row.getId(), LocalDateTime.now().plusSeconds(backoffSec));
            }
        }
    }
}
