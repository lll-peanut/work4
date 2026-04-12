package com.peanut.controller;

import com.peanut.POJO.DTO.VideoReviewLogDTO;
import com.peanut.POJO.entity.Resp;
import com.peanut.POJO.entity.VideoReviewLog;
import com.peanut.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: peanut
 * @date: 2026/4/11
 * @version:1.0
 */
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    private final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping
    public Resp<VideoReviewLog> ReviewVideo(VideoReviewLogDTO videoReviewLogDTO) {
        VideoReviewLog videoReviewLog = adminService.reviewVideo(videoReviewLogDTO);
        logger.info("管理员{}审核视频{}，结果：{}，理由：{}", videoReviewLogDTO.getReviewerId(), videoReviewLogDTO.getVideoId(), videoReviewLogDTO.getDecision().getDesc(), videoReviewLogDTO.getReason());
        return Resp.success(videoReviewLog);
    }


}
