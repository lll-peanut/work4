package com.peanut.im.enumPackage;

/**
 * @author: peanut
 * @date: 2026/5/19
 * @version:1.0 * IM协议类型枚举
 * *
 * * 支持类型：
 * * MSG / SERVER_ACK / DELIVER_ACK / DELIVERED_NOTICE / SYNC_REQ / SYNC_RESP / ERROR
 */

public enum ImWsEnvelopeType {


    /**
     * 聊天消息
     */
    MSG("MSG"),
    /**
     * 服务器确认应答
     */
    SERVER_ACK("SERVER_ACK"),
    /**
     * 送达回执
     */
    DELIVER_ACK("DELIVER_ACK"),
    /**
     * 已送达通知
     */
    DELIVERED_NOTICE("DELIVERED_NOTICE"),
    /**
     * 同步补拉请求
     */
    SYNC_REQ("SYNC_REQ"),
    /**
     * 同步补拉响应
     */
    SYNC_RESP("SYNC_RESP"),
    /**
     * 错误通知
     */
    ERROR("ERROR"),
    /**
     * 未知类型（兼容扩展）
     */
    UNKNOWN("UNKNOWN");

    private final String code;

    ImWsEnvelopeType(String code) {
        this.code = code;
    }

    /**
     * 获取字符串类型
     */
    public String getCode() {
        return code;
    }

    /**
     * 通过字符串获得对应枚举，未定义时返回 UNKNOWN
     */
    public static ImWsEnvelopeType fromCode(String code) {
        if (code == null) return UNKNOWN;
        for (ImWsEnvelopeType t : values()) {
            if (t.code.equalsIgnoreCase(code.trim())) {
                return t;
            }
        }
        return UNKNOWN;
    }

    /**
     * 判断字符串是否合法类型
     */
    public static boolean isValid(String code) {
        return fromCode(code) != UNKNOWN;
    }
}