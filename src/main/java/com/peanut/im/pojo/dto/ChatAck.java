package com.peanut.im.pojo.dto;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */
public class ChatAck {
    private final String type = "ack";
    private String msgId;
    private String clientMsgId;

    public ChatAck() {}

    public ChatAck(String msgId, String clientMsgId) {
        this.msgId = msgId;
        this.clientMsgId = clientMsgId;
    }

    public String getType() {
        return type;
    }

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
}