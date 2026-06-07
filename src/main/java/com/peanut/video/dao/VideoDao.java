package com.peanut.video.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peanut.POJO.DTO.LikeCountIncrementDTO;
import com.peanut.im.enumPackage.VideoStatus;
import com.peanut.video.eneity.pojo.Video;
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

    int insertVideo(Video video);

    /**
     * 批量更新视频点赞数
     * @param videoLikeIncrementList 视频ID-增量 列表
     */
    void batchUpdateVideoLikeCount(@Param("list") List<LikeCountIncrementDTO> videoLikeIncrementList);


    List<Video> selectHomePageVideo(@Param("lastestTime") String lastestTime, @Param("size") int size);

    void updateVideoStatus(@Param("videoId") String videoId, @Param("videoStatus") VideoStatus videoStatus);

    @Select("select * from video where id = #{videoId} and deleted = 0 limit 1")
    Video getVideoById(@Param("videoId") String videoId);

    List<Video> selectVideoPending(@Param("page") int page, @Param("size") int size);

    int updateVideo(Video video);
}
