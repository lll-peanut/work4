package com.peanut.im.pojo.DO;

import com.peanut.im.enumPackage.ConversationType;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/17
 * @version:1.0
 */
public class MessageDO {
    private String id;
    private String conversationId;
    private String fromUserId;
    private String content;
    private Long seq;
    private ConversationType msgType; // 0 normal, 1 system
    private LocalDateTime createdAt;

    public static MessageDO create(String conversationId, String fromUserId, String content, long seq) {
        MessageDO m = new MessageDO();
        m.conversationId = conversationId;
        m.fromUserId = fromUserId;
        m.content = content;
        m.seq = seq;
        m.msgType = ConversationType.USER;
        return m;
    }

    public static MessageDO createSystem(String conversationId, String content, long seq) {
        MessageDO m = new MessageDO();
        m.conversationId = conversationId;
        m.fromUserId = "SYSTEM";
        m.content = content;
        m.seq = seq;
        m.msgType = ConversationType.SYSTEM;
        return m;
    }

    // getter/setter ...


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public ConversationType getMsgType() {
        return msgType;
    }

    public void setMsgType(ConversationType msgType) {
        this.msgType = msgType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}