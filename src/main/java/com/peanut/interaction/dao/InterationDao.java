package com.peanut.interaction.dao;

import com.peanut.POJO.entity.Likes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: peanut
 * @date: 2026/5/6
 * @version:1.0
 */
public interface InterationDao {

    int batchInsert(@Param("list") List<Likes> list);
}
