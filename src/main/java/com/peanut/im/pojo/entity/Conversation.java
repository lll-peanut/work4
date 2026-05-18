package com.peanut.im.pojo.entity;

import com.peanut.im.enumPackage.ConversationType;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/17
 * @version:1.0
 */
public class Conversation {
    private String id;
    private ConversationType type;
    private String peerKey;
    private String name;
    private String avatar;
    private String ownerId;
    private String lastMsgId;
    private Long lastSeq;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Conversation() {
    }

    public Conversation(String id, ConversationType type, String peerKey, String name, String avatar, String ownerId, String lastMsgId, Long lastSeq, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.peerKey = peerKey;
        this.name = name;
        this.avatar = avatar;
        this.ownerId = ownerId;
        this.lastMsgId = lastMsgId;
        this.lastSeq = lastSeq;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Conversation createDm(String id, String peerKey, LocalDateTime serverTime) {
        Conversation c = new Conversation();
        c.id = id;
        c.type = ConversationType.USER;
        c.peerKey = peerKey;
        c.lastSeq = 0L;
        c.createdAt = serverTime;
        c.updatedAt = serverTime;
        return c;
    }

    // getter/setter ...

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getPeerKey() {
        return peerKey;
    }

    public void setPeerKey(String peerKey) {
        this.peerKey = peerKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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