package com.peanut.im.pojo.dto;

import com.peanut.im.enumPackage.ConversationType;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/15
 * @version:1.0
 */
public class ConversationListItemDTO {

    // --- 会话基本信息（来自 conversations） ---
    private String conversationId;
    private ConversationType conversationType;

    /**
     * 群聊：name/avatar；单聊：如果你没做“对方信息拼装”，这里可能为空
     */
    private String conversationName;
    private String conversationAvatar;

    /**
     * 群主（群聊可用）
     */
    private String ownerId;

    /**
     * 会话全局最后消息指针
     */
    private String lastMsgId;
    private Long lastSeq;

    // --- 用户在该会话上的状态（来自 user_conversation） ---
    private Integer pinned;          // 1=置顶,0=不置顶
    private Integer mute;            // 1=免打扰,0=正常
    private Long lastReadSeq;        // 用户读到哪里
    private Integer unreadCount;     // 未读数（冗余）
    private Long listCursorSeq;      // 列表排序游标
    private LocalDateTime listUpdatedAt; // uc.updated_at（会话列表条目更新时间）

    public ConversationListItemDTO(String conversationId, ConversationType conversationType, String conversationName, String conversationAvatar, String ownerId, String lastMsgId, Long lastSeq, Integer pinned, Integer mute, Long lastReadSeq, Integer unreadCount, Long listCursorSeq, LocalDateTime listUpdatedAt) {
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.conversationName = conversationName;
        this.conversationAvatar = conversationAvatar;
        this.ownerId = ownerId;
        this.lastMsgId = lastMsgId;
        this.lastSeq = lastSeq;
        this.pinned = pinned;
        this.mute = mute;
        this.lastReadSeq = lastReadSeq;
        this.unreadCount = unreadCount;
        this.listCursorSeq = listCursorSeq;
        this.listUpdatedAt = listUpdatedAt;
    }

    // --- getters & setters ---

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getConversationAvatar() {
        return conversationAvatar;
    }

    public void setConversationAvatar(String conversationAvatar) {
        this.conversationAvatar = conversationAvatar;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(String lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public Long getLastSeq() {
        return lastSeq;
    }

    public void setLastSeq(Long lastSeq) {
        this.lastSeq = lastSeq;
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

    public LocalDateTime getListUpdatedAt() {
        return listUpdatedAt;
    }

    public void setListUpdatedAt(LocalDateTime listUpdatedAt) {
        this.listUpdatedAt = listUpdatedAt;
    }
}
