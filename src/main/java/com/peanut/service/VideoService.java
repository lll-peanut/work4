package com.peanut.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.DTO.VideoPostDTO;
import com.peanut.POJO.DTO.VideoSearchDTO;
import com.peanut.POJO.entity.POJOList;
import com.peanut.POJO.entity.Video;
import com.peanut.im.enumPackage.VideoStatus;
import org.springframework.ui.Model;

import java.util.List;

public interface VideoService {
    Video getVideo(String videoId);

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

    void updateVideoStatus(String videoId, VideoStatus videoStatus);
}
