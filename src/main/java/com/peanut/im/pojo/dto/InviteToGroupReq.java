package com.peanut.im.pojo.dto;

/**
 * @author: peanut
 * @date: 2026/4/16
 * @version:1.0
 */
public class InviteToGroupReq {
    private String conversationId;   // 群会话id（如果你要支持“建群并拉人”，也可以改成 groupName 等）
    private String inviteeUserId;

    public InviteToGroupReq() {
    }

    public InviteToGroupReq(String conversationId, String inviteeUserId) {
        this.conversationId = conversationId;
        this.inviteeUserId = inviteeUserId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getInviteeUserId() {
        return inviteeUserId;
    }

    public void setInviteeUserId(String inviteeUserId) {
        this.inviteeUserId = inviteeUserId;
    }
}
