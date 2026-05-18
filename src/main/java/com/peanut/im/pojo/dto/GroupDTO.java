package com.peanut.im.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author: peanut
 * @date: 2026/4/24
 * @version:1.0
 */
public class GroupDTO {

    private String name;
    private String avatar;

    public GroupDTO() {
    }

    public GroupDTO(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
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
}
