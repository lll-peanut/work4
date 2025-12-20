package com.peanut.Dao;

import com.peanut.POJO.User;
import org.apache.ibatis.annotations.Select;

public interface UserDao2 {


    @Select("select * from user where id = #{id}")
    public User getUser(String id);


}
