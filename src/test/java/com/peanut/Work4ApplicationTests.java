package com.peanut;

import com.peanut.Dao.SubscribeDao;
import com.peanut.Dao.UserDao;
import com.peanut.Dao.UserDao2;
import com.peanut.service.InterationService;
import com.peanut.service.SocialService;
import com.peanut.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.profiles.active=test" // 激活测试环境Profile
})
class Work4ApplicationTests {

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Autowired
    UserDao2 userDao2;

    @Autowired
    InterationService interationService;

    @Autowired
    SocialService socialService;

    @Autowired
    SubscribeDao subscribeDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
    }
}
