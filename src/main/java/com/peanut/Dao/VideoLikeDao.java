package com.peanut.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peanut.POJO.VideoLike;

import java.util.List;

public interface VideoLikeDao extends BaseMapper<VideoLike> {

    public void batchSaveOrUpdateVideoLike(List<VideoLike> videoLikes);
}
