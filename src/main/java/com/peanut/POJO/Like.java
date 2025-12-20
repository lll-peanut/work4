package com.peanut.POJO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("like_")
public class Like {

    @TableField("user_id")
    private String userId;

    @TableField("video_id")
    private String videoId;

    @TableField("comment_id")
    private String commentId;

    public Like() {
    }

    public Like(String userId, String videoId, String commentId) {
        this.userId = userId;
        this.videoId = videoId;
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public String toString() {
        return "Like{" +
                "userId='" + userId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", commentId='" + commentId + '\'' +
                '}';
    }
}
