package com.peanut.service;

import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.DTO.RelationActionDTO;
import com.peanut.POJO.DTO.UserPageQueryDTO;
import com.peanut.POJO.entity.POJOList;

public interface SocialService {
    void handleRelationAction(String userId, RelationActionDTO relationActionDTO);
    POJOList subscribeList(UserPageQueryDTO userPageQueryDTO);
    POJOList subscriberList(UserPageQueryDTO userPageQueryDTO);
    POJOList friendList(String userId, PageQueryDTO pageQueryDTO);
    void follow(String userId, String toUserId);
    void unfollow(String userId, String toUserId);
}
