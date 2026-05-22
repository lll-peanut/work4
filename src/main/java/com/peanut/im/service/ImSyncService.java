package com.peanut.im.service;

import com.peanut.im.dao.ChatMessageDao;
import com.peanut.im.pojo.dto.ImWsEnvelope;
import com.peanut.im.pojo.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.peanut.im.enumPackage.ImWsEnvelopeType.SYNC_RESP;

/**
 * @author: peanut
 * @date: 2026/5/19
 * @version:1.0
 */
@Service
public class ImSyncService {

    private final ChatMessageDao chatMessageDao;

    @Value("${im.sync.limit:200}")
    private int limit;

    public ImSyncService(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
    }

    public ImWsEnvelope<Object> sync(String userId, String conversationId, long fromSeqExclusive) {
        // 这里可做权限校验：userId 是否在会话里（你 sendMessage 已经做过，sync 也应做）
        List<ChatMessage> list = chatMessageDao.findAfterSeq(conversationId, fromSeqExclusive, limit);

        ImWsEnvelope<Object> resp = new ImWsEnvelope<>();
        resp.setType(SYNC_RESP);
        resp.setConversationId(conversationId);
        resp.setFromSeqExclusive(fromSeqExclusive);
        resp.setPayload(list);
        return resp;
    }
}