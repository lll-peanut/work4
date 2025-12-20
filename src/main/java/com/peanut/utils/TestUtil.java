package com.peanut.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peanut.POJO.DTO.VideoSearchDTO;
import com.peanut.POJO.POJOList;
import com.peanut.POJO.Resp;
import com.peanut.POJO.User;
import com.peanut.POJO.Video;
import com.peanut.annotation.CurrentUserId;
import com.peanut.expection.BusinessException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class TestUtil {

    /**
     * 搜索视频
     * @param videoSearchDTO
     * @param currentUserId
     * @return
     */
//    public POJOList<Video> searchVideo(@Validated VideoSearchDTO videoSearchDTO, String currentUserId) {
//        String keywords = videoSearchDTO.getKeywords();
//        Long fromDate = videoSearchDTO.getFromDate();
//        Long toDate = videoSearchDTO.getToDate();
//        Integer pageNum = videoSearchDTO.getPageNum();
//        Integer pageSize = videoSearchDTO.getPageSize();
//
//        String username = videoSearchDTO.getUsername();
//        if (keywords == null) {
//            throw new BusinessException("未传入keywords");
//        }
//        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
//        if (!keywords.equals("")) {
//            videoQueryWrapper.and(wrapper -> wrapper.like("title", keywords)
//                    .or()
//                    .like("description", keywords));
//        }
//        if (fromDate != null && toDate != null) {
//            System.out.println(fromDate);
//            Instant fromDateInstant = Instant.ofEpochMilli(fromDate);
//            Instant toDateInstant = Instant.ofEpochMilli(toDate);
//            LocalDateTime fromDateTime = LocalDateTime.ofInstant(fromDateInstant, ZoneId.systemDefault());
//            LocalDateTime toDateTime = LocalDateTime.ofInstant(toDateInstant, ZoneId.systemDefault());
//            System.out.println("fromDate:" + fromDateTime);
//            System.out.println("toDate:" + toDateTime);
//            videoQueryWrapper.between("created_at", fromDateTime, toDateTime);
//        }
//        if (username != null) {
//            User user = userDao.selectOne(new QueryWrapper<User>().eq("username", username));
//            if (user == null) {
//                throw new BusinessException("当前搜索用户不存在");
//            }
//            String userId = user.getId();
//            videoQueryWrapper.eq("user_id", userId);
//        }
//        pageSize = PageUtil.getPageSize(pageSize);
//        pageNum = PageUtil.getPageNum(pageNum);
//        Page page = new Page<>(pageNum, pageSize);
//        Page page1 = videoDao.selectPage(page, videoQueryWrapper);
//        List videoList = page1.getRecords();
//        long total = page1.getTotal();
//        saveSearchHistory(videoSearchDTO, currentUserId);
//        return new POJOList<Video>(videoList, total);
//    }
//    @PostMapping("/search")
//    public Resp<POJOList<Video>> searchVideo(@RequestParam(value = "keywords", required = false) String keywords,
//                                             @RequestParam(value = "page_size") Integer pageSize,
//                                             @RequestParam(value = "page_num") Integer pageNum,
//                                             @RequestParam(value = "from_date", required = false) Long fromDateStr,
//                                             @RequestParam(value = "to_date", required = false) Long toDateStr,
//                                             @RequestParam(value = "username", required = false) String username,
//                                             @CurrentUserId String userId) {
//        VideoSearchDTO videoSearchDTO = new VideoSearchDTO(keywords, pageSize, pageNum, fromDateStr, toDateStr, username);
//        POJOList<Video> list = videoService.searchVideo(videoSearchDTO, userId);
//        logger.info("搜索视频成功");
//        return Resp.success(list);
//    }
}
