package com.peanut.im.pojo.DO;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/17
 * @version:1.0
 */
public class UserConversationDO {
    private String userId;
    private String conversationId;
    private Integer pinned;
    private Integer mute;
    private Long lastReadSeq;
    private Integer unreadCount;
    private Long listCursorSeq;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public static UserConversationDO init(String conversationId, String userId, LocalDateTime serverTime) {
        UserConversationDO uc = new UserConversationDO();
        uc.userId = userId;
        uc.conversationId = conversationId;
        uc.pinned = 0;
        uc.mute = 0;
        uc.lastReadSeq = 0L;
        uc.unreadCount = 0;
        uc.listCursorSeq = 0L;
        uc.updatedAt = serverTime;
        uc.createdAt = serverTime;
        return uc;
    }

    public static UserConversationDO initGroup(String conversationId, String userId, long lastReadSeq, LocalDateTime serverTime) {
        UserConversationDO uc = new UserConversationDO();
        uc.userId = userId;
        uc.conversationId = conversationId;
        uc.pinned = 0;
        uc.mute = 0;
        uc.unreadCount = 0;
        uc.listCursorSeq = lastReadSeq;
        uc.updatedAt = serverTime;
        uc.createdAt = serverTime;
        uc.setLastReadSeq(lastReadSeq); // 关键：加入群时读到当前最新
        return uc;
    }

    public static UserConversationDO initWithLastRead(String conversationId, String userId, long lastReadSeq, LocalDateTime serverTime) {
        UserConversationDO uc = init(conversationId, userId, serverTime);
        uc.lastReadSeq = lastReadSeq;
        uc.listCursorSeq = lastReadSeq;
        return uc;
    }

    // getter/setter ...
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public Integer getPinned() { return pinned; }
    public void setPinned(Integer pinned) { this.pinned = pinned; }
    public Integer getMute() { return mute; }
    public void setMute(Integer mute) { this.mute = mute; }
    public Long getLastReadSeq() { return lastReadSeq; }
    public void setLastReadSeq(Long lastReadSeq) { this.lastReadSeq = lastReadSeq; }
    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }
    public Long getListCursorSeq() { return listCursorSeq; }
    public void setListCursorSeq(Long listCursorSeq) { this.listCursorSeq = listCursorSeq; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}