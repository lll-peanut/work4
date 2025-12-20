package com.peanut.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.DTO.UserPageQueryDTO;
import com.peanut.POJO.POJOList;
import com.peanut.POJO.VO.UserVO;
import com.peanut.POJO.UserRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
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
}
