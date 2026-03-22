package com.peanut.im.enumPackage;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 * 消息类型枚举：USER（用户消息）和 SYSTEM（系统消息）。用户消息通常由客户端发送，包含用户生成的内容；系统消息由服务器生成，包含系统通知、公告等信息。
 * 区分消息类型有助于服务器处理不同来源和性质的消息，并为客户端提供适当的展示和交互方式。
 */
public enum MessageType {
    TEXT(1), IMAGE(2), VIDEO(3), FILE(4);
    private final int value;
    MessageType(int value) { this.value = value; }
    public int getValue() { return value; }
    public static MessageType fromValue(int value) {
        for (MessageType t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("未知MessageType值: " + value);
    }
}