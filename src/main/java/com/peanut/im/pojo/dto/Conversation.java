package com.peanut.im.pojo.dto;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/3/15
 * @version:1.0
 */
public class Conversation {

    private String id;

    /**
     * 1=单聊, 2=群聊
     */
    private Integer type;

    /**
     * 群聊标题/可选（单聊通常为 null）
     */
    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 最后一条消息ID（用于会话列表加速）
     */
    private Long lastMessageId;

    /**
     * 最后一条消息时间（用于会话列表排序）
     */
    private LocalDateTime lastMessageAt;

    public Conversation() {}

    public String getId() {
        return id;
    }

    public Conversation setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public Conversation setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Conversation setTitle(String title) {
        this.title = title;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Conversation setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Conversation setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public Conversation setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
        return this;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public Conversation setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        return this;
    }
}