package com.peanut.im.enumPackage;

/**
 * @author: peanut
 * @date: 2026/4/11
 * @version:1.0
 */
public enum VideoStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "审核通过"),
    REJECTED("REJECTED", "审核拒绝"),
    BLOCKED("BLOCKED", "已屏蔽");

    private final String code;
    private final String desc;

    VideoStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
