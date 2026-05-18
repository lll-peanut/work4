package com.peanut.controller;

import com.peanut.POJO.DTO.LikesDTO;
import com.peanut.POJO.entity.Comment;
import com.peanut.POJO.entity.POJOList;
import com.peanut.POJO.entity.Resp;
import com.peanut.video.eneity.pojo.Video;
import com.peanut.annotation.CurrentUserId;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.expection.BusinessException;
import com.peanut.service.InterationService;
import com.peanut.utils.PageUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户互动控制器
 * 功能： 点赞， 获取点赞列表， 评论， 获取评论列表， 删除评论
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@RestController
@RedisLimitOnClassAnnotation(key = "interationController")
public class InterationController {

    @Autowired
    private InterationService interationService;

    private static final Logger logger = LoggerFactory.getLogger(InterationController.class);

    private static final String ERR_EMPTY_LIKE_TARGET = "必须传一个点赞对象（video_id/comment_id）";

    private static final String ERR_DUPLICATE_LIKE_TARGET = "不能同时传入video_id和comment_id";

    private static final String VIDEO_LIKE_SUCCESS = "用户{}对视频{}{}成功";

    private static final String COMMENT_LIKE_SUCCESS = "用户{}对评论{}{}成功";

    private static final String GET_LIKE_LIST = "{}获取点赞列表";

    private static final String COMMENT_LIST = "{}评论成功";

    private static final String LOG_COMMENT_LIST_QUERY_SUCCESS = "{}查询评论列表成功";

    private static final String LOG_COMMENT_DELETE_SUCCESS = "{}删除评论{}成功";

    private static final String OPERATION_LIKE = "点赞";

    private static final String OPERATION_CANCEL_LIKE = "取消点赞";

    @PostMapping("/like/action")
    public Resp like(@Valid @RequestBody LikesDTO likesDTO,
                     @CurrentUserId String userId) {
            interationService.like(likesDTO, userId);
            logger.info(userId + "点赞操作成功");
        return Resp.success(null);
    }

    @GetMapping("/like/list")
    public Resp<POJOList<Video>> likeList(
            @RequestParam("user_id") String userId,
            @RequestParam("page_size") Integer pageSize,
            @RequestParam("page_num") Integer pageNum,
            @CurrentUserId String currentUserId) {
        PageUtil.validate(pageNum, pageSize);
        POJOList<Video> likeVideos = interationService.getLikeVideos(userId, pageNum, pageSize);
        logger.info(GET_LIKE_LIST, currentUserId);
        return Resp.success(likeVideos);
    }

    @PostMapping("/comment/publish")
    public Resp comment(@RequestParam(value = "video_id", required = false) String videoId,
                        @RequestParam(value = "comment_id", required = false) String commentId,
                        @RequestParam("content") String content,
                        @CurrentUserId String userId) {
        interationService.comment(userId, videoId, commentId, content);
        logger.info(COMMENT_LIST, userId);
        return Resp.success(null);
    }

    @GetMapping("/comment/list")
    public Resp<POJOList<Comment>> getCommentList(@RequestParam(value = "video_id", required = false) String videoId,
                                                  @RequestParam(value = "comment_id", required = false) String commentId,
                                                  @RequestParam("page_size") int pageSize,
                                                  @RequestParam("page_num") int pageNum,
                                                  @CurrentUserId String currentUserId) {
        validateVideoIdAndCommentId(videoId, commentId);
        PageUtil.validate(pageNum, pageSize);
        List<Comment> commentList = interationService.getCommentList(videoId, commentId, pageNum, pageSize);
        logger.info(LOG_COMMENT_LIST_QUERY_SUCCESS, currentUserId);
        return Resp.success(new POJOList<>(commentList, null));
    }

    @DeleteMapping("/comment/delete")
    public Resp deleteComment(@RequestParam(value = "video_id") String videoId,
                              @RequestParam(value = "comment_id") String commentId,
                              @CurrentUserId String userId) {
        boolean hasVideoId = StringUtils.hasText(videoId);
        boolean hasCommentId = StringUtils.hasText(commentId);

        if (!hasVideoId && !hasCommentId) {
            throw new BusinessException(ERR_EMPTY_LIKE_TARGET);
        }
        interationService.deleteComment(userId, videoId, commentId);
        logger.info(LOG_COMMENT_DELETE_SUCCESS, userId, commentId);
        return Resp.success(null);
    }

    private void validateVideoIdAndCommentId(String videoId, String commentId) {

        boolean hasVideoId = StringUtils.hasText(videoId);
        boolean hasCommentId = StringUtils.hasText(commentId);

        if (!hasVideoId && !hasCommentId) {
            throw new BusinessException(ERR_EMPTY_LIKE_TARGET);
        }
        if (hasVideoId && hasCommentId) {
            throw new BusinessException(ERR_DUPLICATE_LIKE_TARGET);
        }
    }
}
