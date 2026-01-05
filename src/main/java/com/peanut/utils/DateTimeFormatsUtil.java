package com.peanut.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author: peanut
 * @date: 2026/3/2
 * @version:1.0
 * 日期时间格式工具类
 * 统一管理项目中使用的日期时间格式，避免硬编码字符串散布在代码中
 */
public class DateTimeFormatsUtil {
    private DateTimeFormatsUtil() {}

    /** yyyy-MM-dd HH:mm:ss */
    public static final DateTimeFormatter YMD_HMS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取指定时区的日期时间格式化器
     * @return 日期时间格式化器
     */
    public static DateTimeFormatter getFormatterWithZone() {
        return YMD_HMS.withZone(ZoneId.of("Asia/Shanghai"));
    }


    /**
     * 将LocalDateTime转换为Redis ZSet可用的score（毫秒时间戳，double类型）
     * @param localDateTime 需要转换的时间
     * @param zoneId 时区
     * @return 毫秒时间戳，double类型（建议用于ZSet的score）
     */
    public static double toZSetScore(LocalDateTime localDateTime, ZoneId zoneId) {
        return (double) localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 默认获取 Asia/Shanghai 时区的score
     */
    public static double toZSetScore(LocalDateTime localDateTime) {
        return toZSetScore(localDateTime, ZoneId.of("Asia/Shanghai"));
    }
}
