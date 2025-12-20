package com.peanut.POJO.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class UserVO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String avatarurl;

    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserVO() {
    }

    public UserVO(String id, String avatarurl, String username) {
        this.id = id;
        this.avatarurl = avatarurl;
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "id='" + id + '\'' +
                ", avatarurl='" + avatarurl + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
