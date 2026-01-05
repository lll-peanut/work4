package com.peanut.Dao;

import com.peanut.POJO.CommentLike;
import com.peanut.POJO.VideoLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: peanut
 * @date: 2026/1/6
 * @version:1.0
 */
public interface CommentLikeDao {

    void batchSaveOrUpdateCommentLike(List<CommentLike> commentLikes);


    /**
     * 通过已有的commentLike查看数据库中该用户对评论的点赞情况
     *
     * @param commentLikes
     * @return
     */
    List<CommentLike> batchQueryByUserAndComment(@Param("commentLikes") List<CommentLike> commentLikes);
}
