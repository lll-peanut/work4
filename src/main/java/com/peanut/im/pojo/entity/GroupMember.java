package com.peanut.im.pojo.entity;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/3/23
 * @version:1.0
 */
public class GroupMember {

    private String id; // 主键，唯一标识

    private String groupId; // 群组ID

    private String userId; // 用户ID

    private int role; // 1=普通成员, 2=管理员, 3=群主

    private LocalDateTime joinedAt; // 加群时间

    private int status; // 1=正常成员, 0=退群/被踢

    public GroupMember() {}

    public GroupMember(String id, String groupId, String userId, int role, LocalDateTime joinedAt, int status) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
