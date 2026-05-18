package com.peanut.utils;

public class RedisUtil {

    public static final String VIDEO_LIKE = "video_like";
    public static final String COMMENT_LIKE = "comment_like";

    public static String getKey(String userId, String id, int type) {
        return "likes" + ":" + userId + ":" + type + ":" + id;
    }
}
