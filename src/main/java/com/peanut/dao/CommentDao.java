package com.peanut.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.peanut.POJO.entity.Comment;
import com.peanut.POJO.DTO.LikeCountIncrementDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentDao extends BaseMapper<Comment> {

    /**
     * 批量更新评论点赞数
     * @param likeIncrementList 评论ID-增量 列表
     */
    void batchUpdateCommentLikeCount(@Param("list") List<LikeCountIncrementDTO> likeIncrementList);
}
