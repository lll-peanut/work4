package com.peanut.im.enumPackage;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0
 * 消息类型枚举：USER（用户消息）、GROUP（群消息）、SYSTEM（系统消息）。
 */
public enum ConversationType {
    USER(1), SYSTEM(2), GROUP(3);
    private final int value;
    ConversationType(int value) { this.value = value; }
    public int getValue() { return value; }
    public static ConversationType fromValue(int value) {
        for (ConversationType t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("未知ConversationType值: " + value);
    }
}
