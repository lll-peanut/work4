package com.peanut.video.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.entity.Base;
import com.peanut.POJO.entity.POJOList;
import com.peanut.POJO.entity.Resp;
import com.peanut.annotation.CurrentUserId;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.expection.BusinessException;
import com.peanut.service.UserService;
import com.peanut.utils.DateTimeFormatsUtil;
import com.peanut.utils.FilePersistenceUtil;
import com.peanut.video.eneity.pojo.Video;
import com.peanut.video.eneity.dto.VideoPostDTO;
import com.peanut.video.eneity.dto.VideoSearchDTO;
import com.peanut.video.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * 视频管理控制器
 * 实现功能排行榜，投稿，发布列表，搜索视频
 *
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@RestController
@RequestMapping("/video")
@RedisLimitOnClassAnnotation
public class VideoController {

    private final static Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilePersistenceUtil filePersistenceUtil;

    /**
     * 上传视频以及视频封面
     *
     * @param file
     * @param publishDTO
     * @param cover
     * @param userId
     * @return 返回已提交的提示，确认视频状态要在task中获取
     */
    @PostMapping("/publish")
    public Resp video(@RequestParam("data") MultipartFile file,
                      VideoPostDTO publishDTO,
                      @RequestParam("cover") MultipartFile cover,
                      @CurrentUserId String userId) {
        try {
            String taskId = UUID.randomUUID().toString();
            validateUploadFile(file, cover, taskId);
            String videoFilePath = filePersistenceUtil.persistFile(file, taskId, "video");
            String coverFilePath = filePersistenceUtil.persistFile(cover, taskId, "cover");
            videoService.postVideoAndCover(publishDTO, userId, videoFilePath, coverFilePath, taskId);
            logger.info("视频投稿任务已提交 | userId: {} | taskId: {}", userId, taskId);
            return Resp.success("视频投稿任务已提交，正在处理中" +
                    "taskId" + taskId + "tips" + "可通过/task/status/" + taskId + "查询结果");
        } catch (Exception e) {
            // 其他异常
            String errorMsg = "视频投稿接口异常：" + e.getMessage();
            logger.error("视频投稿接口异常 | userId: {}", userId, e);
            return Resp.fail(new Base(500, errorMsg));
        }
    }


    /**
     * 检验视频和封面
     * @param videoFile
     * @param cover
     * @param taskId
     */
    private void validateUploadFile(MultipartFile videoFile, MultipartFile cover, String taskId) {
        // 校验视频文件
        if (videoFile == null || videoFile.isEmpty()) {
            throw new BusinessException("视频文件不能为空" + taskId);
        }
        if (videoFile.getSize() > 1024 * 1024 * 100) { // 限制100MB
            throw new BusinessException("视频文件大小不能超过100MB" + taskId);
        }
        // 校验封面文件
        if (cover == null || cover.isEmpty()) {
            throw new BusinessException("封面文件不能为空" + taskId);
        }
        // 校验文件格式（示例：只允许mp4/avi视频，jpg/png封面）
        String videoContentType = videoFile.getContentType();
        if (!Arrays.asList("video/mp4", "video/avi", "video/webm").contains(videoContentType)) {
            throw new BusinessException("视频格式仅支持mp4/avi/webm" + taskId);
        }
        String coverContentType = cover.getContentType();
        if (!Arrays.asList("image/jpeg", "image/png").contains(coverContentType)) {
            throw new BusinessException("封面格式仅支持jpg/png" + taskId);
        }
    }

    @GetMapping("/list")
    public Resp<POJOList> list(@RequestParam("user_id") String userId,
                               @RequestParam("page_num") Integer pageNum,
                               @RequestParam("page_size") Integer pageSize) {
        IPage<Video> videoIPage = videoService.selectVideoByPage(userId, pageNum, pageSize);
        List<Video> videoList = videoIPage.getRecords();
        long total = videoIPage.getTotal();
        POJOList videoList1 = new POJOList(videoList, total);
        logger.info("搜索发布列表成功");
        return Resp.success(videoList1);
    }

    @PostMapping("/search")
    public Resp<POJOList<Video>> searchVideo(@RequestParam(value = "keywords", required = false) String keywords,
                                             @RequestParam(value = "page_size") Integer pageSize,
                                             @RequestParam(value = "page_num") Integer pageNum,
                                             @RequestParam(value = "from_date", required = false) Long fromDateStr,
                                             @RequestParam(value = "to_date", required = false) Long toDateStr,
                                             @RequestParam(value = "username", required = false) String username,
                                             @CurrentUserId String userId) {
        // todo 映射问题
        VideoSearchDTO videoSearchDTO = new VideoSearchDTO(keywords, pageSize, pageNum, fromDateStr, toDateStr, username);
        POJOList<Video> list = videoService.searchVideo(videoSearchDTO, userId);
        logger.info("搜索视频成功");
        return Resp.success(list);
    }

    @GetMapping("/popular")
    public Resp<POJOList<Video>> rankingList(PageQueryDTO pageQueryDTO,
                                             @CurrentUserId(required = false) String userId) {
        POJOList<Video> videoPOJOList = videoService.rankingList(pageQueryDTO);
        logger.info(userId + "：查看排行榜成功");
        return Resp.success(videoPOJOList);
    }

    @GetMapping("/feed/")
    public Resp<List<Video>> videoStream(@RequestParam(value = "latest_time", required = false) String latestTime,
                                             @CurrentUserId(required = false) String userId) {
        String time = null;
        if (latestTime != null) {
            Long s = Long.parseLong(latestTime);
            time = DateTimeFormatsUtil
                    .getFormatterWithZone()
                    .format(Instant.ofEpochMilli(s));
        }
        List<Video> videos = videoService.getVideos(time, userId);
        return Resp.success(videos);
    }
}
