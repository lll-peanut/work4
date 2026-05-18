package com.peanut.im.pojo.entity;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/22
 * @version:1.0
 */
public class DmPairs {

    private String userMin;

    private String userMax;

    private String conversationId;

    private LocalDateTime createdAt;

    protected DmPairs() {
    }

    public DmPairs(String userMin, String userMax, String conversationId, LocalDateTime createdAt) {
        this.userMin = userMin;
        this.userMax = userMax;
        this.conversationId = conversationId;
        this.createdAt = createdAt;
    }

    public String getUserMin() {
        return userMin;
    }

    public void setUserMin(String userMin) {
        this.userMin = userMin;
    }

    public String getUserMax() {
        return userMax;
    }

    public void setUserMax(String userMax) {
        this.userMax = userMax;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
