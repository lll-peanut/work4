package com.peanut.im.enumPackage;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.peanut.enumPackage.CodeDescEnum;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0 消息类型枚举：USER（用户消息）、GROUP（群消息）、SYSTEM（系统消息）。
 */
public enum ConversationType implements CodeDescEnum<String> {
    USER("USER", "用户消息"),
    SYSTEM("SYSTEM", "系统消息"),
    GROUP("GROUP", "群消息");

    private final String code;
    private final String desc;

    ConversationType(String code, String desc) {
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
