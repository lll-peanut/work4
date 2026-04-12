package com.peanut.im.pojo.entity;

import com.peanut.im.enumPackage.ConversationType;
import com.peanut.im.enumPackage.MessageType;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */

public class ChatMessage {
    // todo 找个时间把toTargetId删了

    public ChatMessage(String msgId, String clientMsgId, String conversationId, String fromUserId, String toTargetId, LocalDateTime serverTime, String content, MessageType msgType, Long sequence, ConversationType conversationType) {
        this.msgId = msgId;
        this.clientMsgId = clientMsgId;
        this.conversationId = conversationId;
        this.fromUserId = fromUserId;
        this.toTargetId = toTargetId;
        this.serverTime = serverTime;
        this.content = content;
        this.msgType = msgType;
        this.sequence = sequence;
        this.conversationType = conversationType;
    }

    public ChatMessage() {}

    private String msgId;

    private String clientMsgId;

    private String conversationId;

    private String fromUserId;

    private String toTargetId;

    private LocalDateTime serverTime;

    private String content;

    private MessageType msgType;

    private Long sequence;

    private ConversationType conversationType;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getClientMsgId() {
        return clientMsgId;
    }

    public void setClientMsgId(String clientMsgId) {
        this.clientMsgId = clientMsgId;
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

    public String getToTargetId() {
        return toTargetId;
    }

    public void setToTargetId(String toTargetId) {
        this.toTargetId = toTargetId;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public void setServerTime(LocalDateTime serverTime) {
        this.serverTime = serverTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "msgId='" + msgId + '\'' +
                ", clientMsgId='" + clientMsgId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", toTargetId='" + toTargetId + '\'' +
                ", serverTime=" + serverTime +
                ", content='" + content + '\'' +
                ", msgType=" + msgType +
                ", sequence=" + sequence +
                ", conversationType=" + conversationType +
                '}';
    }
}
