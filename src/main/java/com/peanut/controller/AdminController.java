package com.peanut.controller;

import com.peanut.video.eneity.dto.VideoReviewLogDTO;
import com.peanut.POJO.entity.Resp;
import com.peanut.video.eneity.pojo.Video;
import com.peanut.video.eneity.pojo.VideoReviewLog;
import com.peanut.annotation.CurrentUserId;
import com.peanut.service.AdminService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: peanut
 * @date: 2026/4/11
 * @version:1.0
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/review")
    public Resp<VideoReviewLog> ReviewVideo(@Valid @RequestBody VideoReviewLogDTO videoReviewLogDTO) {
        VideoReviewLog videoReviewLog = adminService.reviewVideo(videoReviewLogDTO);
        logger.info("管理员{}审核视频{}，结果：{}，理由：{}", videoReviewLogDTO.getReviewerId(), videoReviewLogDTO.getVideoId(), videoReviewLogDTO.getDecision().getDesc(), videoReviewLogDTO.getReason());
        return Resp.success(videoReviewLog);
    }

    @GetMapping("/list")
    public Resp<List<Video>> getVideoList(int page, int size, @CurrentUserId String adminId) {
        List<Video> videoList = adminService.getVideoList(page, size);
        logger.info("管理员{}获取待审核视频列表，页码：{}，每页大小：{}", adminId, page, size);
        return Resp.success(videoList);
    }


}
