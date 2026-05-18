package com.peanut.video.eneity.dto;

import com.peanut.im.enumPackage.VideoStatus;
import jakarta.validation.constraints.NotNull;

/**
 * @author: peanut
 * @date: 2026/4/12
 * @version:1.0
 */
public class VideoReviewLogDTO {
    public VideoReviewLogDTO() {}

    public VideoReviewLogDTO(String videoId, String reviewerId, VideoStatus decision, String reason) {
        this.videoId = videoId;
        this.reviewerId = reviewerId;
        this.decision = decision;
        this.reason = reason;
    }

    @NotNull
    private String videoId;

    @NotNull
    private String reviewerId;

    @NotNull
    private VideoStatus decision;

    private String reason;

    // Getter and Setter methods

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
