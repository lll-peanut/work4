package com.peanut.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peanut.POJO.VO.UserVO;
import com.peanut.POJO.entity.UserRelation;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface UserRelationDao extends BaseMapper<UserRelation> {

    Integer selectUserIdandToUserId(String userId, String toUserId);

    Integer insertOrUpdateFollow(
            UserRelation userRelation
    );

    Integer insertOrUpdateUnFollow(
            UserRelation userRelation
    );

    Long getFriendsCountById(@Param("id") String id);

    ArrayList<UserVO> getFriendsById(String userId, Integer pageNum, Integer pageSize);

    ArrayList<UserVO> getsubscriberById(@Param("userId") String userId, Integer pageNum, Integer pageSize);

    Long getsubscriberCountById(@Param("id") String id);

    ArrayList<UserVO> getSubscribeById(@Param("userId") String userId, Integer pageNum, Integer pageSize);

    Long getSubscribeCountById(@Param("id") String id);

    int ifFriends(@Param("userId") String userId, @Param("otherUserId") String otherUserId);
}
