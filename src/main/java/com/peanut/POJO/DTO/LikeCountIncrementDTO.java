package com.peanut.POJO.DTO;

/**
 * 视频点赞数增量DTO
 * @author: peanut
 * @date: 2026/1/2
 * @version:1.0
 */
public class LikeCountIncrementDTO {
    private String id;       // ID
    private Integer totalIncrement; // 点赞数增量

    public LikeCountIncrementDTO() {}

    public LikeCountIncrementDTO(String id, Integer totalIncrement) {
        this.id = id;
        this.totalIncrement = totalIncrement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTotalIncrement() {
        return totalIncrement;
    }

    public void setTotalIncrement(Integer totalIncrement) {
        this.totalIncrement = totalIncrement;
    }
}
