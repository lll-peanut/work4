package com.peanut.controller;

import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.DTO.RelationActionDTO;
import com.peanut.POJO.DTO.UserPageQueryDTO;
import com.peanut.POJO.VO.UserVO;
import com.peanut.POJO.entity.Base;
import com.peanut.POJO.entity.POJOList;
import com.peanut.POJO.entity.Resp;
import com.peanut.annotation.CurrentUserId;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 用户社交控制器
 * 功能： 互关，取关，关注，获取粉丝、好友、关注列表
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@RestController
@RedisLimitOnClassAnnotation(key = "socialController")
public class SocialController {

    @Autowired
    private SocialService socialService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);



    @PostMapping("friends/chat")
    public List<String> friendsChat(@RequestParam String userId,
                                    @RequestParam Integer PageNum,
                                    @RequestParam Integer PageSize,
                                    @CurrentUserId String thisUserId) {
        ArrayList<String> list = new ArrayList<>();
        String key = "user:userId:" + thisUserId + ":userId:" + userId + ":toUserId";
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        Set<String> value = zSetOperations.range(key, (PageNum - 1) * PageSize, PageNum * PageSize - 1);
        for (String str : value) {
            String message = (String) stringRedisTemplate.opsForHash().get(key + ":" + str, "message");
            list.add(message);
            stringRedisTemplate.opsForHash().put(key + ":" + str, "isRead", "1");
        }
        return list;
    }

    @PostMapping("friends/unread")
    public List<String> friendsUnread(@RequestParam String userId,
                                      @RequestParam Integer PageNum,
                                      @RequestParam Integer PageSize,
                                      @CurrentUserId String currentUserId) {
        String key = "user:userId:" + currentUserId + ":userId:" + userId + ":toUserId";
        Set<String> zset = stringRedisTemplate.opsForZSet().range(key, 0, -1);
        ArrayList<String> list = new ArrayList<>();
        for (String str : zset) {
            String isRead = (String) stringRedisTemplate.opsForHash().get(key + ":" + str, "isRead");
            if ("0".equals(isRead)) {
                String message = (String) stringRedisTemplate.opsForHash().get(key + ":" + str, "message");
                stringRedisTemplate.opsForHash().put(key + ":" + str, "isRead", "1");
                list.add(message);
            }
        }
        return list;
    }

    @PostMapping("group/chat")
    public List<String> groupsChat(@RequestParam("groupId") String groupId,
                                   @RequestParam Integer PageNum,
                                   @RequestParam Integer PageSize,
                                   @CurrentUserId String thisUserId) {
        ArrayList<String> list = new ArrayList<>();
        SetOperations<String, String> setOperations = stringRedisTemplate.opsForSet();
        Set<String> members = setOperations.members("group:groupId:" + groupId + ":groupId");
        for (String str : members) {
            ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
            String s = opsForValue.get("group:groupId:" + groupId + ":groupId:" + str + ":userId");
            if (s != null) {
                int num = Integer.parseInt(s);
                HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
                for (Integer i = 1; i <= num; i++) {
                    String message = (String) hashOperations.get("group:groupId:" + groupId + ":groupId:" + str + ":userId:" + i.toString(), "message");
                    list.add(message);
                }

            }
        }

        return null;
    }

    @PostMapping("relation/action")
    public Resp subscribe(@Validated RelationActionDTO relationActionDTO,
                          @CurrentUserId String userId) {
        if (userId.equals(relationActionDTO.getTo_user_id())) {
            return Resp.fail(new Base(400, "不能关注或取关自己"));
        }
        socialService.handleRelationAction(userId, relationActionDTO);
        logger.info("{}{}{}成功",userId, relationActionDTO.getAction_type() == 1 ? "关注" : "取关", relationActionDTO.getTo_user_id());
        return Resp.success(null);
    }

    @GetMapping("following/list")
    public Resp<POJOList<UserVO>> getSubscribeList(@Validated UserPageQueryDTO userPageQueryDTO,
                                                   @CurrentUserId String currentUserId) {
        POJOList pojoList = socialService.subscribeList(userPageQueryDTO);
        logger.info("{}查询{}关注列表成功", currentUserId, userPageQueryDTO.getUser_id());
        return Resp.success(pojoList);
    }

    @GetMapping("follower/list")
    public Resp<POJOList<UserVO>> getSubscriberList(@Validated UserPageQueryDTO userPageQueryDTO,
                                                    @CurrentUserId String currentUserId) {
        POJOList pojoList = socialService.subscriberList(userPageQueryDTO);
        logger.info("{}查询{}粉丝列表成功", currentUserId, userPageQueryDTO.getUser_id());
        return Resp.success(pojoList);
    }

    @GetMapping("/friends/list")
    public Resp<POJOList<UserVO>> getFriendsList(@Validated PageQueryDTO pageQueryDTO,
                                                 @CurrentUserId String userId) {
        POJOList pojoList = socialService.friendList(userId, pageQueryDTO);
        logger.info("{}查询好友列表成功", userId);
        return Resp.success(pojoList);
    }


}
