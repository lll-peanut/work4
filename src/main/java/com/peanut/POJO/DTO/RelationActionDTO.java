package com.peanut.POJO.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class RelationActionDTO {
    @NotBlank(message = "被关注用户ID不能为空")
    private String to_user_id;

    @NotNull(message = "操作类型不能为空")
    @Min(value = 0, message = "操作类型只能是0（关注）或1（取关）")
    @Max(value = 1, message = "操作类型只能是0（关注）或1（取关）")
    private Integer action_type;

    public RelationActionDTO(String to_user_id, Integer action_type) {
        this.to_user_id = to_user_id;
        this.action_type = action_type;
    }

    public RelationActionDTO() {}

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public Integer getAction_type() {
        return action_type;
    }

    public void setAction_type(Integer action_type) {
        this.action_type = action_type;
    }
}
