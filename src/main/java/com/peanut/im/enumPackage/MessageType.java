package com.peanut.im.enumPackage;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.peanut.enumPackage.CodeDescEnum;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0 消息类型枚举：USER（用户消息）和 SYSTEM（系统消息）。用户消息通常由客户端发送，包含用户生成的内容；系统消息由服务器生成，包含系统通知、公告等信息。
 * 区分消息类型有助于服务器处理不同来源和性质的消息，并为客户端提供适当地展示和交互方式。
 */
public enum MessageType implements CodeDescEnum {
    TEXT("TEXT", "文本"),
    IMAGE("IMAGE", "图片"),
    VIDEO("VIDEO", "视频"),
    FILE("FILE", "文件");

    private final String code;
    private final String desc;

    MessageType(String code, String desc) {
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