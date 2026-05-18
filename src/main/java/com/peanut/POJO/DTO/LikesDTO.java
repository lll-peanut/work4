package com.peanut.POJO.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author: peanut
 * @date: 2026/5/3
 * @version:1.0
 */
public class LikesDTO {

    @NotNull
    private int type;         // 点赞目标类型

    @NotNull
    private String targetId;      // 关联目标ID

    @Max(1)
    @Min(0)
    private int status;          // 状态（1-有效/点赞，0-取消/无效）

    public LikesDTO() {
    }

    public LikesDTO(int type, String targetId, int status) {
        this.type = type;
        this.targetId = targetId;
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
