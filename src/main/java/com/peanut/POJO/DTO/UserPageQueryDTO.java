package com.peanut.POJO.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class UserPageQueryDTO extends PageQueryDTO {

    @NotBlank(message = "userId不能为空")
    private String user_id;

    public UserPageQueryDTO(Integer page_num, Integer page_size, String user_id) {
        super(page_num, page_size);
        this.user_id = user_id;
    }

    public UserPageQueryDTO() {}

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
