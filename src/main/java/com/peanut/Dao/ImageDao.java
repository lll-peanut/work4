package com.peanut.Dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: peanut
 * @date: 2026/3/3
 * @version:1.0
 */
public interface ImageDao {

    @Select("SELECT url FROM image_feature WHERE md5 = #{md5} LIMIT 1")
    String findUrlByMd5(@Param("md5") String md5);
}
