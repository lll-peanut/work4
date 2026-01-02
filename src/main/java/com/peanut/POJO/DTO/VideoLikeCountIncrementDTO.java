package com.peanut.POJO.DTO;

/**
 * 视频点赞数增量DTO
 * @author: peanut
 * @date: 2026/1/2
 * @version:1.0
 */
public class VideoLikeCountIncrementDTO {
    private String videoId;       // 视频ID
    private Integer totalIncrement; // 点赞数增量

    public VideoLikeCountIncrementDTO() {}

    public VideoLikeCountIncrementDTO(String videoId, Integer totalIncrement) {
        this.videoId = videoId;
        this.totalIncrement = totalIncrement;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Integer getTotalIncrement() {
        return totalIncrement;
    }

    public void setTotalIncrement(Integer totalIncrement) {
        this.totalIncrement = totalIncrement;
    }
}
