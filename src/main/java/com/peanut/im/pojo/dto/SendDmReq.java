package com.peanut.im.pojo.dto;

/**
 * @author: peanut
 * @date: 2026/4/16
 * @version:1.0
 */
public class SendDmReq {
    private String toUserId;
    private String content;

    public SendDmReq() {
    }

    public SendDmReq(String toUserId, String content) {
        this.toUserId = toUserId;
        this.content = content;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
