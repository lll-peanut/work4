package com.peanut.service;

import com.peanut.POJO.Comment;
import com.peanut.POJO.POJOList;
import com.peanut.POJO.Video;

import java.util.List;

public interface InterationService {

    void likeVideo(String userId, String videoId, Integer isLike);

    POJOList<Video> getLikeVideos(String userId, int pageNum, int pageSize);

    void likeComment(String userId, String commentId, Integer isLike);

    List<Video> getLikeComments(String userId, int pageNum, int pageSize);

    void comment(String userId, String videoId, String commentId, String content);

    List<Comment> getCommentList(String videoId, String commentId, int pageNum, int pageSize);

    void deleteComment(String userId, String videoId, String commentId);

    void likeToMysql();

    void commentLikeToMysql();
}
