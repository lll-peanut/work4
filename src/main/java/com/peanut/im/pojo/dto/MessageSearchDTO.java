package com.peanut.im.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.peanut.im.enumPackage.ConversationType;
import com.peanut.im.enumPackage.MessageType;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author: peanut
 * @date: 2026/3/29
 * @version:1.0
 */
public class MessageSearchDTO {

    /**
     * 会话ID
     */
    @NotNull(message = "会话ID conversationId不能为空")
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 消息类型（TEXT、IMAGE、VIDEO、FILE等）
     */
    @JsonProperty("message_type")
    private MessageType messageType;

    /**
     * 会话类型（USER、GROUP、SYSTEM等）
     */
    @NotNull(message = "会话类型conversationType不能为空")
    @JsonProperty("conversation_type")
    private ConversationType conversationType;

    /**
     * 页码（必填，范围0-100）
     */
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

    @Value("${im.history.size:20}")
    private int size;

    public MessageSearchDTO() {}

    public MessageSearchDTO(String conversationId, MessageType messageType, Integer pageNum, Long fromDate, Long toDate) {
        this.conversationId = conversationId;
        this.messageType = messageType;
        this.pageNum = pageNum;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
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

    public int getSize() {
        return size;
    }
}
