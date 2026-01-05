package com.peanut.Dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peanut.POJO.DTO.LikeCountIncrementDTO;
import com.peanut.POJO.Video;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface VideoDao extends BaseMapper<Video> {


    @Update("UPDATE video SET like_count = like_count + 1 WHERE id = #{video_id}")
    int increaseLikeNum(@Param("video_id") String videoId);

    @Select("select 1 from `video` where `id` = #{videoId} and deleted = 0 limit 1")
    Boolean selectVideoById(@Param("videoId") String videoId);

    List<Video> selectVideoListById(@Param("ids") List<String> ids);

    /**
     * 批量更新视频点赞数
     * @param videoLikeIncrementList 视频ID-增量 列表
     */
    void batchUpdateVideoLikeCount(@Param("list") List<LikeCountIncrementDTO> videoLikeIncrementList);
}
