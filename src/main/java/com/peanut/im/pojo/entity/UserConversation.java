package com.peanut.im.pojo.entity;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/14
 * @version:1.0
 */
public class UserConversation {
    private String userId;
    private String conversationId;

    /** 1=置顶, 0=不置顶 */
    private Integer pinned;

    /** 1=免打扰, 0=正常 */
    private Integer mute;

    /** 用户读到的 seq */
    private Long lastReadSeq;

    /** 未读数（可选冗余） */
    private Integer unreadCount;

    /** 列表排序游标：通常=会话 last_seq；置顶可配合 pinned + 该字段排序 */
    private Long listCursorSeq;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserConversation(){}

    public UserConversation(String userId, String conversationId, Integer pinned, Integer mute, Long lastReadSeq, Integer unreadCount, Long listCursorSeq, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.pinned = pinned;
        this.mute = mute;
        this.lastReadSeq = lastReadSeq;
        this.unreadCount = unreadCount;
        this.listCursorSeq = listCursorSeq;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- getters & setters ---

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getPinned() {
        return pinned;
    }

    public void setPinned(Integer pinned) {
        this.pinned = pinned;
    }

    public Integer getMute() {
        return mute;
    }

    public void setMute(Integer mute) {
        this.mute = mute;
    }

    public Long getLastReadSeq() {
        return lastReadSeq;
    }

    public void setLastReadSeq(Long lastReadSeq) {
        this.lastReadSeq = lastReadSeq;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getListCursorSeq() {
        return listCursorSeq;
    }

    public void setListCursorSeq(Long listCursorSeq) {
        this.listCursorSeq = listCursorSeq;
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
