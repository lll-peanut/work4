package com.peanut.Dao;

import org.apache.ibatis.annotations.Select;

public interface GroupDao {
    @Select("select 1 from `group` where `id` = #{id}")
    public Integer getId(String id);
}
