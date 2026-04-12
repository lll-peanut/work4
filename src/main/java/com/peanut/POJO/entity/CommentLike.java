package com.peanut.POJO.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CommentLike {
    @TableField("user_id")
    private String userId;

    @TableField("comment_id")
    private String commentId;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @JsonIgnore
    @TableField("is_cancel")
    private Integer isCancel;

    public CommentLike() {}

    public CommentLike(String userId, String commentId, String id, Integer isCancel) {
        this.userId = userId;
        this.commentId = commentId;
        this.id = id;
        this.isCancel = isCancel;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    @Override
    public String toString() {
        return "CommentLike{" +
                "userId='" + userId + '\'' +
                ", commentId='" + commentId + '\'' +
                ", id='" + id + '\'' +
                ", isCancel=" + isCancel +
                '}';
    }
}
