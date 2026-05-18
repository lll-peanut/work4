package com.peanut.interaction;

import com.peanut.POJO.entity.RedisLikeKeys;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: peanut
 * @date: 2026/5/6
 * @version:1.0
 */
@Component
public class LikeStreamInit {

    private final StringRedisTemplate redisTemplate;

    public LikeStreamInit(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        String streamKey = RedisLikeKeys.LIKE_EVENTS_STREAM;
        String group = RedisLikeKeys.LIKE_EVENTS_GROUP;

        try {
            // MKSTREAM：没有 stream 就创建
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), group);
        } catch (Exception e) {
            // group 已存在会抛异常，忽略即可
        }
    }
}