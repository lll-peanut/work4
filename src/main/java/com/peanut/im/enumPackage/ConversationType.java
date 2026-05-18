package com.peanut.im.enumPackage;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.peanut.enumPackage.CodeDescEnum;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0 类型枚举：USER（用户）、GROUP（群）、SYSTEM（系统）。
 */
public enum ConversationType implements CodeDescEnum<String> {
    USER("USER", "用户"),
    SYSTEM("SYSTEM", "系统"),
    GROUP("GROUP", "群");

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
