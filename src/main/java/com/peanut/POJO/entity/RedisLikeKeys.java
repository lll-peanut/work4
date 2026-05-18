package com.peanut.POJO.entity;

/**
 * @author: peanut
 * @date: 2026/5/6
 * @version:1.0
 */
public final class RedisLikeKeys {
    private RedisLikeKeys() {}

    public static final String LIKE_EVENTS_STREAM = "like:events";
    public static final String LIKE_EVENTS_GROUP = "like_group";

    // type=1 视频举例；也可以把 type 传进来
    public static String targetLikeKey(int type, String targetId) {
        return "like:target:" + type + ":" + targetId;
    }

    public static String userLikeKey(String userId, int type) {
        return "like:user:" + userId + ":" + type;
    }

    public static String rankKey(int type) {
        return "like:rank:" + type;
    }
}