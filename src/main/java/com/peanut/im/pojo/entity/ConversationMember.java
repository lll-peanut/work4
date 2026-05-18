package com.peanut.im.pojo.entity;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/14
 * @version:1.0
 */
public class ConversationMember {
    private String conversationId;
    private String userId;

    /** 群聊: owner/admin/member；单聊: member */
    private String role;

    /** 入群时间/建立单聊成员关系时间 */
    private LocalDateTime joinTime;

    /** 是否已退出/被踢：建议 1=正常, 0=已退出, 2=被踢（按你们定义） */
    private Integer state;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- getters & setters ---

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}