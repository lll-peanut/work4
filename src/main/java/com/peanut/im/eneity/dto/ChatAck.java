package com.peanut.im.eneity.dto;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */
public class ChatAck {
    private String type = "ack";
    private String msgId;

    public ChatAck() {}
    public ChatAck(String msgId) { this.msgId = msgId; }

    public String getType() { return type; }
    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }
}
