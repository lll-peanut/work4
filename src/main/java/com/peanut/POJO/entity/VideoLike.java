package com.peanut.POJO.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class VideoLike {
    @TableField("user_id")
    private String userId;

    @TableField("video_id")
    private String videoId;
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @JsonIgnore
    @TableField("is_cancel")
    private Integer isCancel;

    public VideoLike(String userId, String videoId, String id, Integer isCancel) {
        this.userId = userId;
        this.videoId = videoId;
        this.id = id;
        this.isCancel = isCancel;
    }

    public VideoLike(String userId, String videoId, String id) {
        this.userId = userId;
        this.videoId = videoId;
        this.id = id;
    }

    public VideoLike() {}

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer isCancel() {
        return isCancel;
    }

    public void setCancel(Integer cancel) {
        isCancel = cancel;
    }

    @Override
    public String toString() {
        return "VideoLike{" +
                "userId='" + userId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", id='" + id + '\'' +
                ", isCancel=" + isCancel +
                '}';
    }
}
