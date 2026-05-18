package com.peanut.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peanut.expection.BusinessException;
import com.peanut.im.enumPackage.VideoStatus;
import com.peanut.video.dao.VideoReviewLogDao;
import com.peanut.video.eneity.pojo.Video;
import com.peanut.video.eneity.pojo.VideoReviewLog;
import com.peanut.video.eneity.dto.VideoReviewLogDTO;
import com.peanut.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN')")
    public VideoReviewLog reviewVideo(VideoReviewLogDTO videoReviewLogDto) {
        String videoId = videoReviewLogDto.getVideoId();
        Video video = videoService.getVideo(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在或已被软删除" + videoId);
        }
        if (video.getStatus() != VideoStatus.PENDING) {
            throw new BusinessException("视频已审核过，无法再次审核" + videoId);
        }
        VideoStatus videoStatus = videoReviewLogDto.getDecision();
        if (videoStatus == VideoStatus.APPROVED) {
            video.setStatus(VideoStatus.APPROVED);
            video.setVideourl(video.getVideoOriUrl());
            video.setCoverurl(video.getCoverOriUrl());
            videoService.updateVideo(video);
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

    @PreAuthorize("hasRole('ADMIN')")
    public List<Video> getVideoList(int page, int size) {
        List<Video> pendingVideos = videoService.getPendingVideos(page, size);
        return pendingVideos;
    }
}
