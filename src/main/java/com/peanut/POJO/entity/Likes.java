package com.peanut.POJO.entity;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/5/3
 * @version:1.0
 */
public class Likes {
    private String id;            // 主键ID
    private int type;         // 点赞目标类型
    private String targetId;      // 关联目标ID
    private String userId;        // 点赞用户ID
    private int status;          // 状态（1-有效/点赞，0-取消/无效）
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;

    public Likes() {
    }


    public Likes(String id, int type, String targetId, String userId, int status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.targetId = targetId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
