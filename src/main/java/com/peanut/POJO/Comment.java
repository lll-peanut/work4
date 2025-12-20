package com.peanut.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Comment {

    /**
     * 是否删除
     */
    private int isDeleted;
    /**
     * 子评论数
     */
    @TableField(value = "child_count")
    @JsonProperty("child_count")
    private long childCount;
    /**
     * 评论文本，建议进行一定的文本处理
     */
    private String content;
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
     * 评论 ID，唯一标识符，可选自增/雪花/UUID/其他
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 点赞数，评论点赞的数量
     */
    @TableField(value = "like_count")
    @JsonProperty("like_count")
    private long likeCount;
    /**
     * 父评论 ID，父评论的唯一标识符
     */
    @TableField(value = "parent_id")
    @JsonProperty("parent_id")
    private String parentid;
    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    /**
     * 发表者 ID，发表评论的用户唯一标识符
     */
    @TableField(value = "user_id")
    @JsonProperty("user_id")
    private String userid;
    /**
     * 视频 ID，视频的唯一标识符
     */
    @TableField(value = "video_id")
    @JsonProperty("video_id")
    private String videoid;

    public Comment() {}

    public Comment(long childCount, String content, LocalDateTime createdAt, LocalDateTime deletedAt, String id, long likeCount, String parentid, LocalDateTime updatedAt, String userid, String videoid) {
        this.childCount = childCount;
        this.content = content;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.id = id;
        this.likeCount = likeCount;
        this.parentid = parentid;
        this.updatedAt = updatedAt;
        this.userid = userid;
        this.videoid = videoid;
    }

    public long getChildCount() {
        return childCount;
    }

    public void setChildCount(long childCount) {
        this.childCount = childCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
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

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
