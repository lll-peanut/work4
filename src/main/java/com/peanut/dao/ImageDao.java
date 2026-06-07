package com.peanut.dao;

import com.peanut.POJO.entity.Image;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: peanut
 * @date: 2026/3/3
 * @version:1.0
 */
public interface ImageDao {

    @Select("SELECT * FROM image WHERE md5 = #{md5} LIMIT 1")
    Image findUrlByMd5(@Param("md5") String md5);

    @Insert("""
            INSERT INTO image (id, md5, url, created_at)
            VALUES (#{id}, #{md5}, #{url}, #{createdAt})
            """)
    int insertImage(Image image);
}
