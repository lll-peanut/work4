package com.peanut.im.pojo;

/**
 * @author: peanut
 * @date: 2026/4/20
 * @version:1.0
 */
public class JoinGroupResponse {
    public String groupId;
    public boolean joined;         // 本次是否发生了加入动作
    public boolean alreadyMember;  // 是否原本就是成员

    public JoinGroupResponse() {}

    public JoinGroupResponse(String groupId, boolean joined, boolean alreadyMember) {
        this.groupId = groupId;
        this.joined = joined;
        this.alreadyMember = alreadyMember;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public boolean isAlreadyMember() {
        return alreadyMember;
    }

    public void setAlreadyMember(boolean alreadyMember) {
        this.alreadyMember = alreadyMember;
    }
}
