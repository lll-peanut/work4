package com.peanut.POJO.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

public class UserRelation {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 当前用户ID
     */
    private String userId;

    /**
     * 关联用户ID
     */
    private String toUserId;

    /**
     * 状态（默认0）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


    public UserRelation(String id, String userId, String toUserId, Integer status,
                        LocalDateTime createdAt, LocalDateTime deletedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.toUserId = toUserId;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.updatedAt = updatedAt;
    }
    public UserRelation(String userId, String toUserId) {
        this.userId = userId;
        this.toUserId = toUserId;
    }

    public UserRelation(String id, String userId, String toUserId) {
        this.userId = userId;
        this.toUserId = toUserId;
        this.id = id;
    }


    public UserRelation() {}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}