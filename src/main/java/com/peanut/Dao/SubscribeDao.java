package com.peanut.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peanut.POJO.Subscribe;
import com.peanut.POJO.VO.UserVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SubscribeDao extends BaseMapper<Subscribe> {


    @Select("select username, avatarurl, user.id from user join subscribe as s1 join subscribe as s2 on\n" +
            "    s1.to_user_id = s2.id and s1.id = s2.to_user_id\n" +
            "        and s1.id = #{id} where user.id = s2.id\n")
    IPage<UserVO> getFriendById(Page<?> page, @Param("id") String id);

    @Select("select user.id , user.avatarurl, user.username from user join subscribe on subscribe.to_user_id = #{id}\n" +
            "            and user.id = subscribe.id")
    IPage<UserVO> getsubscriberById(Page<?> page, @Param("id") String id);

    @Select("select user.avatarurl, username, user.id from `user`\n" +
            "    , subscribe where subscribe.id = #{id} and subscribe.to_user_id = user.id")
    IPage<UserVO> getSubscribeById(Page<?> page, @Param("id") String id);
}
