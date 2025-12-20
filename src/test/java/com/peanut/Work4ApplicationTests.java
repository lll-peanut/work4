package com.peanut;

import com.peanut.Dao.SubscribeDao;
import com.peanut.Dao.UserDao;
import com.peanut.Dao.UserDao2;
import com.peanut.POJO.User;
import com.peanut.service.InterationService;
import com.peanut.service.SocialService;
import com.peanut.service.UserService;
import com.peanut.utils.TokenUtil;
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
        User user = userDao.selectById("1916150790730260482");
        User user1 = userDao.selectById("1916160479987732481");
        User user2 = userDao.selectById("1920031441611083778");
//        if (user != null) {
//            System.out.println(TokenUtil.sign(user.getUsername(), user.getId()));
//            System.out.println(user.getPassword());
//            System.out.println(user);
//        }
        String sign = TokenUtil.sign(user.getUsername(), user.getId());
        System.out.println(sign);
        System.out.println(TokenUtil.sign(user1.getUsername(), user1.getId()));
        System.out.println(TokenUtil.sign(user2.getUsername(), user2.getId()));
    }

    @Test
    void redisTest() {
//        ZSetOperations<String, String> stringStringZSetOperations =
//                stringRedisTemplate.opsForZSet();
//        String userId = "123";
//        String key = "user:userId:" + userId + ":userId:" + "5672:" + "toUserId";
//        String content = "好难";
//        stringRedisTemplate.opsForHash().put(key, "message", content);
//        stringRedisTemplate.opsForHash().put(key, "isRead", "0");
//        long time = System.currentTimeMillis();
//        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
//        // 倒序查询第一个元素（score最大）
//        Set<ZSetOperations.TypedTuple<String>> tuples = zSetOps.reverseRangeWithScores(key, 0, 0);
//        System.out.println("666");
//        String field = "";
//        if (tuples != null && !tuples.isEmpty()) {
//            field = tuples.iterator().next().getValue();
//        } else {
//            field = "1";
//        }
//        System.out.println(field);
//        stringStringZSetOperations.add(key, key, time);
    }
}
