package com.peanut.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peanut.POJO.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao extends BaseMapper<User> {

    @Select("select id, username, avatarurl, created_at, deleted_at, updated_at, role from `user` where id = #{id}")
    public User selectInfo(String id);

    List<String> selectExistIds(@Param("ids") List<String> ids);

    void updateMFASecret(@Param("userId") String userId, @Param("secret") String secret);
}
