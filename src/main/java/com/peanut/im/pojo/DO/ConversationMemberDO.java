package com.peanut.im.pojo.DO;

import com.peanut.im.enumPackage.ConvRoleType;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/17
 * @version:1.0
 */
public class ConversationMemberDO {
    private String id;
    private String conversationId;
    private String userId;
    private ConvRoleType role;
    private LocalDateTime joinTime;
    private Integer state; // 0 normal

    public static ConversationMemberDO join(String conversationId, String userId, ConvRoleType role, LocalDateTime joinTime) {
        ConversationMemberDO m = new ConversationMemberDO();
        m.conversationId = conversationId;
        m.userId = userId;
        m.role = role;
        m.state = 0;
        m.joinTime = joinTime;
        return m;
    }

    public ConversationMemberDO(String id, String conversationId, String userId, ConvRoleType role, LocalDateTime joinTime, Integer state) {
        this.id = id;
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
        this.joinTime = joinTime;
        this.state = state;
    }

    public ConversationMemberDO() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ConvRoleType getRole() {
        return role;
    }

    public void setRole(ConvRoleType role) {
        this.role = role;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
