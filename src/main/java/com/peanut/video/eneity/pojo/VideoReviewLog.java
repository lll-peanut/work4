package com.peanut.video.eneity.pojo;

/**
 * @author: peanut
 * @date: 2026/4/11
 * @version:1.0
 */
import com.peanut.im.enumPackage.VideoStatus;

import java.time.LocalDateTime;

public class VideoReviewLog {
    public VideoReviewLog() {}

    public VideoReviewLog(String id, String videoId, String reviewerId, LocalDateTime createdAt, VideoStatus decision, String reason) {
        this.id = id;
        this.videoId = videoId;
        this.reviewerId = reviewerId;
        this.createdAt = createdAt;
        this.decision = decision;
        this.reason = reason;
    }

    private String id;

    private String videoId;

    private String reviewerId;

    private LocalDateTime createdAt;

    private VideoStatus decision;

    private String reason;

    // Getter and Setter methods

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public VideoStatus getDecision() {
        return decision;
    }

    public void setDecision(VideoStatus decision) {
        this.decision = decision;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}