package com.peanut.utils;

public class RedisUtil {

    public static final String VIDEO_LIKE = "video_like";
    public static final String COMMENT_LIKE = "comment_like";

    public static String getKey(String userId, String id) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userId);
        stringBuilder.append("::");
        stringBuilder.append(id);
        return stringBuilder.toString();
    }
}
