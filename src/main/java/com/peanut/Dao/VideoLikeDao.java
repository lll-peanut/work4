package com.peanut.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peanut.POJO.VideoLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideoLikeDao extends BaseMapper<VideoLike> {

    void batchSaveOrUpdateVideoLike(List<VideoLike> videoLikes);


    /**
     * 通过已有的videoLike查看数据库中该用户对视频的点赞情况
     *
     * @param videoLikes
     * @return
     */
    List<VideoLike> batchQueryByUserAndVideo(@Param("videoLikes") List<VideoLike> videoLikes);
}
