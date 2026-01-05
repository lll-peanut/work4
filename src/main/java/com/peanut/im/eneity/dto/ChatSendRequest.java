package com.peanut.im.eneity.dto;

import com.peanut.im.enumPackage.MessageType;
import com.peanut.im.enumPackage.TargetType;

/**
 *
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 * 消息发送请求DTO，包含消息内容、目标用户ID、消息类型和投递目标类型等信息，用于客户端向服务器发送消息请求。
 */
public class ChatSendRequest {
    /**
     * 单发时的目标用户ID；广播时可为空
     */
    private String toUserId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 客户端消息唯一ID，用于幂等和回执
     */
    private String clientMsgId;

    /**
     * 消息类型：USER / SYSTEM
     */
    private MessageType msgType;

    /**
     * 投递目标类型：SINGLE / BROADCAST
     */
    private TargetType targetType;

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getClientMsgId() { return clientMsgId; }
    public void setClientMsgId(String clientMsgId) { this.clientMsgId = clientMsgId; }

    public MessageType getMsgType() { return msgType; }
    public void setMsgType(MessageType msgType) { this.msgType = msgType; }

    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
}
