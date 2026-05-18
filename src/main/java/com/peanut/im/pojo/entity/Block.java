package com.peanut.im.pojo.entity;

import com.peanut.utils.IdUtil;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/4/21
 * @version:1.0
 */
public class Block {

    private String id;

    private String actorType;

    private String actorId;

    private String targetId;

    private String targetType;

    private String scopeType;

    private String scopeId;

    private int status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // --- Constructors ---
    public Block() {
    }

    public Block(String id, String actorType, String actorId, String targetId, String targetType, String scopeType, String scopeId, int status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.actorType = actorType;
        this.actorId = actorId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.scopeType = scopeType;
        this.scopeId = scopeId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Block createDmBlock(String actorType, String actorId, String targetId, String targetType, String scopeType, String scopeId, LocalDateTime serverTime) {
        String id = IdUtil.getId();
        Block block = new Block();
        block.id = id;
        block.actorType = actorType;
        block.actorId = actorId;
        block.targetId = targetId;
        block.targetType = targetType;
        block.scopeType = scopeType;
        block.scopeId = scopeId;
        block.status = 0;
        block.createdAt = serverTime;
        block.updatedAt = serverTime;
        block.deletedAt = null;
        return block;
    }

// --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}