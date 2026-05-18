package com.peanut.im.pojo.dto;

/**
 * @author: peanut
 * @date: 2026/4/16
 * @version:1.0
 */
public class SendMessageResultDTO {
    private String conversationId;
    private String msgId;
    private long seq;

    public SendMessageResultDTO() {
    }

    public SendMessageResultDTO(String conversationId, String msgId, long seq) {
        this.conversationId = conversationId;
        this.msgId = msgId;
        this.seq = seq;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
