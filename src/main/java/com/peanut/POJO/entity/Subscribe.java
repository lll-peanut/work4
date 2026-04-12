package com.peanut.POJO.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Subscribe {

    @JsonIgnore
    String id;

    @TableField(value = "to_user_id")
    String toUserId;

    public Subscribe(String id, String to_user_id) {
        this.id = id;
        this.toUserId = to_user_id;
    }

    public Subscribe() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}
