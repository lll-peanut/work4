package com.peanut.video.eneity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.peanut.im.enumPackage.VideoStatus;

import java.time.LocalDateTime;

public class Video {
    /**
     * 评论数，评论数量
     */
    @TableField(value = "comment_count")
    @JsonProperty("comment_count")
    private Integer commentCount;
    /**
     * 封面，封面链接
     */
    @TableField(value = "cover_url")
    @JsonProperty("cover_url")
    private String coverurl;
    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    /**
     * 删除时间
     */
    @TableField(value = "deleted_at")
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
    /**
     * 视频描述，视频描述
     */
    private String description;
    /**
     * 视频id，唯一标识符，可选自增/雪花/UUID/其他
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 点赞数，点赞数量
     */
    @TableField(value = "like_count")
    @JsonProperty("like_count")
    private Integer likeCount;
    /**
     * 视频标题，视频标题
     */
    private String title;
    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    /**
     * 视频作者，发表视频的用户唯一标识符
     */
    @TableField(value = "user_id")
    @JsonProperty("user_id")
    private String userid;
    /**
     * 视频，视频文件链接
     */
    @TableField(value = "video_url")
    @JsonProperty("video_url")
    private String videourl;
    /**
     * 访问量，视频访问量
     */
    @TableField(value = "visit_count")
    @JsonProperty("visit_count")
    private Integer visitCount;

    /**
     * 视频状态
     */
    @TableField(value = "status")
    @JsonProperty("status")
    private VideoStatus status;

    @TableField(value = "video_ori_url")
    @JsonProperty("video_ori_url")
    private String videoOriUrl;

    @TableField(value = "cover_ori_url")
    @JsonProperty("cover_ori_url")
    private String coverOriUrl;

    private int deleted;

    public Video(Integer commentCount, String coverurl, LocalDateTime createdAt, LocalDateTime deletedAt, String description, String id, Integer likeCount, String title, LocalDateTime updatedAt, String userid, String videourl, Integer visitCount, VideoStatus status, String videoOriUrl, String coverOriUrl, int deleted) {
        this.commentCount = commentCount;
        this.coverurl = coverurl;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.description = description;
        this.id = id;
        this.likeCount = likeCount;
        this.title = title;
        this.updatedAt = updatedAt;
        this.userid = userid;
        this.videourl = videourl;
        this.visitCount = visitCount;
        this.status = status;
        this.videoOriUrl = videoOriUrl;
        this.coverOriUrl = coverOriUrl;
        this.deleted = deleted;
    }

    public Video() {
        likeCount = 0;
        commentCount = 0;
        visitCount = 0;
        status = VideoStatus.PENDING;
        deleted = 0;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getCoverurl() {
        return coverurl;
    }

    public void setCoverurl(String coverurl) {
        this.coverurl = coverurl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public VideoStatus getStatus() {
        return status;
    }

    public void setStatus(VideoStatus status) {
        this.status = status;
    }

    public String getVideoOriUrl() {
        return videoOriUrl;
    }

    public void setVideoOriUrl(String videoOriUrl) {
        this.videoOriUrl = videoOriUrl;
    }

    public String getCoverOriUrl() {
        return coverOriUrl;
    }

    public void setCoverOriUrl(String coverOriUrl) {
        this.coverOriUrl = coverOriUrl;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Video{" +
                "commentCount=" + commentCount +
                ", coverurl='" + coverurl + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", likeCount=" + likeCount +
                ", title='" + title + '\'' +
                ", updatedAt=" + updatedAt +
                ", userid='" + userid + '\'' +
                ", videourl='" + videourl + '\'' +
                ", visitCount=" + visitCount +
                ", status=" + status +
                ", videoOriUrl='" + videoOriUrl + '\'' +
                ", coverOriUrl='" + coverOriUrl + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}