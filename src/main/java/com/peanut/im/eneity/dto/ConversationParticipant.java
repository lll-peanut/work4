package com.peanut.im.eneity.dto;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/3/15
 * @version:1.0
 */
public class ConversationParticipant {

    /**
     * 会话ID（字符串）
     */
    private String conversationId;

    /**
     * 用户ID（字符串）
     */
    private String userId;

    private Integer role;
    private LocalDateTime joinedAt;

    private Boolean isBlocked;
    private LocalDateTime blockedAt;

    private Boolean isMuted;
    private Boolean isPinned;

    /**
     * 已读到哪条消息
     */
    private String lastReadMessageId;

    private LocalDateTime lastReadAt;

    private Integer unreadCount;

    public String getConversationId() {
        return conversationId;
    }

    public ConversationParticipant setConversationId(String conversationId) {
        this.conversationId = conversationId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ConversationParticipant setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Integer getRole() {
        return role;
    }

    public ConversationParticipant setRole(Integer role) {
        this.role = role;
        return this;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public ConversationParticipant setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
        return this;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public ConversationParticipant setIsBlocked(Boolean blocked) {
        isBlocked = blocked;
        return this;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public ConversationParticipant setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
        return this;
    }

    public Boolean getIsMuted() {
        return isMuted;
    }

    public ConversationParticipant setIsMuted(Boolean muted) {
        isMuted = muted;
        return this;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public ConversationParticipant setIsPinned(Boolean pinned) {
        isPinned = pinned;
        return this;
    }

    public String getLastReadMessageId() {
        return lastReadMessageId;
    }

    public ConversationParticipant setLastReadMessageId(String lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
        return this;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public ConversationParticipant setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
        return this;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public ConversationParticipant setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
        return this;
    }
}