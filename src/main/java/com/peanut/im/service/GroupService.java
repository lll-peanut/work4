package com.peanut.im.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author: peanut
 * @date: 2026/3/24
 * @version:1.0
 */
@Service
public class GroupService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Set<String> getGroupMembersUserIds(String groupId) {
        String key = "group:members:" + groupId;
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        System.out.println(members);
        return stringRedisTemplate.opsForSet().members(key);
    }
}
