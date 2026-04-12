package com.peanut.im.enumPackage;

import com.peanut.enumPackage.CodeDescEnum;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 * 投递目标类型枚举：SINGLE（单发）和 BROADCAST（广播）。
 * 单发表示消息仅发送给一个特定用户，通常需要指定目标用户ID；广播表示消息发送给所有在线用户，不需要指定目标用户ID。
 */
public enum TargetType implements CodeDescEnum<String> {
    SINGLE("SINGLE", "单发"),
    BROADCAST("BROADCAST", "广播");

    private final String code;
    private final String desc;

    TargetType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}