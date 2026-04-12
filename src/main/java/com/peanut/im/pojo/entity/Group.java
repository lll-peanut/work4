package com.peanut.im.pojo.entity;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/3/23
 * @version:1.0
 */
public class Group {


    private String id; // 群组唯一ID

    private String name; // 群组名称

    private String description; // 群简介

    private String ownerId; // 群主（用户ID）

    private LocalDateTime createdAt; // 创建时间

    private int status; // 状态: 1=正常, 0=解散

    public Group() {}

    public Group(String id, String name, String description, String ownerId, LocalDateTime createdAt, int status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

