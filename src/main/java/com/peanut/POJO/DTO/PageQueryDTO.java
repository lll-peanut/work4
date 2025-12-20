package com.peanut.POJO.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PageQueryDTO {

    @Max(value = 100, message = "页码最多到100页")
    @Min(value = 0, message = "页码必须大于等于0")
    @NotNull(message = "页码不存在")
    private Integer page_num;

    @Max(value = 50, message = "条数必须小于等于50")
    @Min(value = 1, message = "条数必须大于等于1")
    @NotNull(message = "条数不存在")
    private Integer page_size;

    // 构造方法参数同步改为下划线
    public PageQueryDTO(Integer page_num, Integer page_size) {
        this.page_num = page_num;
        this.page_size = page_size;
    }

    public PageQueryDTO() {}

    public Integer getPage_num() {
        return page_num;
    }

    public void setPage_num(Integer page_num) {
        this.page_num = page_num;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }
}
