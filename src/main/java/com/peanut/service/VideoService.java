package com.peanut.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.DTO.VideoPostDTO;
import com.peanut.POJO.DTO.VideoSearchDTO;
import com.peanut.POJO.POJOList;
import com.peanut.POJO.Video;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    Model getVideo(Model model);

    String postVideo(String file);

    String postVideoCover(String file);

    void insertVideo(Video video);

    IPage<Video> selectVideoByPage(String id, int page, int size);

    void postVideoAndCover(VideoPostDTO videoPostDTO, String id, String video, String cover, String taskId);

    POJOList<Video> searchVideo(VideoSearchDTO videoSearchDTO, String userId);

    POJOList<Video> getAndSetRank();

    List<Video> getVideoList(List<String> ids);

    POJOList<Video> rankingList(PageQueryDTO pageQueryDTO);

    List<Video> getVideos(String lasestTime, String userId);
}
