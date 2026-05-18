package com.peanut.im.pojo.DO;

import com.peanut.im.enumPackage.ConversationType;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/20
 * @version:1.0
 */
public class GroupVO {
    private String id;
    private ConversationType type;
    private String name;
    private String avatar;
    private String ownerId;
    private LocalDateTime createdAt;

    public GroupVO() {
    }

    public GroupVO(String id, ConversationType type, String name, String avatar, String ownerId, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.avatar = avatar;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
