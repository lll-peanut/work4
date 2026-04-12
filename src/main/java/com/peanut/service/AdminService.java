package com.peanut.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.POJO.DTO.VideoReviewLogDTO;
import com.peanut.POJO.entity.Video;
import com.peanut.POJO.entity.VideoReviewLog;
import com.peanut.dao.VideoReviewLogDao;
import com.peanut.expection.BusinessException;
import com.peanut.im.enumPackage.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: peanut
 * @date: 2026/4/11
 * @version:1.0
 */
@Service
public class AdminService {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoReviewLogDao videoReviewLogDao;

    @PreAuthorize("hasRole('ADMIN')")
    public VideoReviewLog reviewVideo(VideoReviewLogDTO videoReviewLogDto) {
        String videoId = videoReviewLogDto.getVideoId();
        Video video = videoService.getVideo(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在或已被软删除" + videoId);
        } else if (video.getStatus() != VideoStatus.PENDING) {
            throw new BusinessException("视频已审核过，无法再次审核" + videoId);
        }
        VideoStatus videoStatus = videoReviewLogDto.getDecision();
        if (videoStatus == VideoStatus.APPROVED) {
            videoService.updateVideoStatus(videoId, VideoStatus.APPROVED);
        } else if (videoStatus == VideoStatus.REJECTED) {
            videoService.updateVideoStatus(videoId, VideoStatus.REJECTED);
        } else if (videoStatus == VideoStatus.BLOCKED) {
            videoService.updateVideoStatus(videoId, VideoStatus.BLOCKED);
        } else {
            throw new BusinessException("无效的视频审核状态" + videoId);
        }
        String id = IdWorker.getIdStr();
        LocalDateTime time = LocalDateTime.now();
        VideoReviewLog videoReviewLog = new VideoReviewLog(id, videoId, videoReviewLogDto.getReviewerId(), time, videoStatus, videoReviewLogDto.getReason());
        videoReviewLogDao.insertVideoReview(videoReviewLog);
        return videoReviewLog;
    }

    public List<Video> getVideoList() {

        // 这里可以添加获取视频列表的具体逻辑
        return null;
    }
}
