package com.peanut.POJO.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 视频搜索DTO
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
public class VideoSearchDTO {

    /**
     * 搜索关键词（非必填，若传值则去除首尾空格，长度限制）
     */
    @Pattern(regexp = "^\\s*$|^.{1,100}$", message = "关键词不能为空或长度超过100字符")
    private String keywords;

    /**
     * 每页条数（必填，范围1-100）
     */
    @NotNull(message = "每页条数pageSize不能为空")
    @Min(value = 1, message = "每页条数pageSize最小为1")
    @Max(value = 100, message = "每页条数pageSize最大为100")
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 页码（必填，范围0-100）
     */
    @NotNull(message = "页码pageNum不能为空")
    @Min(value = 0, message = "页码pageNum最小为0")
    @Max(value = 100, message = "页码pageNum最大为100")
    @JsonProperty("page_num")
    private Integer pageNum;

    /**
     * 开始时间戳（非必填，若传值则为正数，且小于结束时间戳）
     */
    @JsonProperty("from_date")
    @TableField("from_date")
    @PositiveOrZero(message = "开始时间戳fromDate必须为非负数")
    private Long fromDate;

    /**
     * 结束时间戳（非必填，若传值则为正数，且大于开始时间戳）
     */
    @JsonProperty("to_date")
    @TableField("to_date")
    @PositiveOrZero(message = "结束时间戳toDate必须为非负数")
    private Long toDate;

    /**
     * 用户名（非必填，若传值则符合用户名规则：字母/数字/下划线，2-20位）
     */
    @Pattern(regexp = "^\\s*$|^[a-zA-Z0-9_]{2,20}$",
            message = "用户名不能为空或格式错误（仅允许字母、数字、下划线，2-20位）")
    private String username;

    public VideoSearchDTO(String keywords, Integer pageSize, Integer pageNum, Long fromDate, Long toDate, String username) {
        this.keywords = keywords;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.username = username;
    }

    public VideoSearchDTO() {}

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
