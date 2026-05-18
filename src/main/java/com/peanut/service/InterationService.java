package com.peanut.service;

import com.peanut.POJO.DTO.LikesDTO;
import com.peanut.POJO.entity.Comment;
import com.peanut.POJO.entity.POJOList;
import com.peanut.video.eneity.pojo.Video;

import java.util.List;

public interface InterationService {

    POJOList<Video> getLikeVideos(String userId, int pageNum, int pageSize);

    List<Video> getLikeComments(String userId, int pageNum, int pageSize);

    void comment(String userId, String videoId, String commentId, String content);

    List<Comment> getCommentList(String videoId, String commentId, int pageNum, int pageSize);

    void deleteComment(String userId, String videoId, String commentId);

    void likesToMysql();

    public void like(LikesDTO likesDTO, String userId);


}
