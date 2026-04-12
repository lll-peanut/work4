package com.peanut.im.pojo.dto;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 * 聊天消息封装类，包含消息ID、客户端消息ID、会话ID、发送者用户ID、接收者用户ID、服务器时间戳和消息内容等字段。
 * 该类用于在服务器和客户端之间传递消息数据，确保消息的完整
 */
public class ChatEnvelope {
    private String msgId;
    private String clientMsgId;
    private String conversationId;
    private String fromUserId;
    private String toUserId;
    private LocalDateTime serverTime;
    private String content;
    private Long sequence; // 消息序列号，用于消息排序和去重
    private Integer conversationType; // 消息类型：1=私聊，2=群聊等

    public Integer getConversationType() {
        return conversationType;
    }

    public void setConversationType(Integer conversationType) {
        this.conversationType = conversationType;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }

    public String getClientMsgId() { return clientMsgId; }
    public void setClientMsgId(String clientMsgId) { this.clientMsgId = clientMsgId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public void setServerTime(LocalDateTime serverTime) {
        this.serverTime = serverTime;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
