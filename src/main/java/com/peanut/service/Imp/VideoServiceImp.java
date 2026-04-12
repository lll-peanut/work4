package com.peanut.service.Imp;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peanut.POJO.entity.AsyncTaskResult;
import com.peanut.POJO.entity.POJOList;
import com.peanut.POJO.entity.User;
import com.peanut.POJO.entity.Video;
import com.peanut.dao.UserDao;
import com.peanut.dao.VideoDao;
import com.peanut.POJO.DTO.PageQueryDTO;
import com.peanut.POJO.DTO.VideoPostDTO;
import com.peanut.POJO.DTO.VideoSearchDTO;
import com.peanut.expection.BusinessException;
import com.peanut.expection.SystemException;
import com.peanut.im.enumPackage.VideoStatus;
import com.peanut.service.VideoService;
import com.peanut.utils.FilePersistenceUtil;
import com.peanut.utils.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Validated
public class VideoServiceImp implements VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${base.path}")
    private String basePath;

    @Value("${param.homepage.video.size}")
    private int homepageVideoSize;

    private static final String COVER_FOLDER = "/file/cover/upload/";
    private static final String VIDEO_FOLDER = "/file/video/upload/";

    // Redis ZSET 键常量（视频点击量：key=video:visit:count，field=视频ID，score=点击量）
    private static final String VIDEO_VISIT_RANK_KEY = "video:visit:count";

    private static final Logger log = LoggerFactory.getLogger(VideoService.class);


    @Override
    public Video getVideo(String videoId) {
        Video videoById = videoDao.getVideoById(videoId);
        return videoById;
    }

    /**
     * 异步上传视频和封面
     *
     * @param videoPostDTO
     * @param userId
     * @param videoFilePath
     * @param coverFilePath
     * @param taskId
     */
    @Override
    @Async("videoUploadExecutor")
    public void postVideoAndCover(VideoPostDTO videoPostDTO, String userId, String videoFilePath, String coverFilePath, String taskId) {
        String videoPath = null;
        String coverPath = null;
        try {
            redisTemplate.opsForValue().set("video:task:" + taskId,
                    JSON.toJSONString(AsyncTaskResult.process("进行中", taskId)),
                    3600, TimeUnit.SECONDS);
            LocalDateTime timestamp = LocalDateTime.now();
            Video video = new Video();
            BeanUtils.copyProperties(videoPostDTO, video);
            video.setUpdatedAt(timestamp);
            video.setCreatedAt(timestamp);
            log.info("开始执行视频投稿异步任务 | taskId: {} | userId: {} | 视频名: {} | 封面名: {}",
                    taskId, userId, videoFilePath, coverFilePath);
            videoPath = postVideo(videoFilePath);
            coverPath = postVideoCover(coverFilePath);
            video.setUserid(userId);
            video.setCoverurl(coverPath);
            video.setVideourl(videoPath);
            insertVideo(video);
            redisTemplate.opsForValue().set("video:task:" + taskId,
                    JSON.toJSONString(AsyncTaskResult.success("成功", taskId)),
                    3600, TimeUnit.SECONDS);
            log.info("视频投稿异步任务执行成功 | taskId: {} | videoId: {} | userId: {}",
                    taskId, video.getId(), userId);
            FilePersistenceUtil.deletePersistedFile(videoFilePath);
            FilePersistenceUtil.deletePersistedFile(coverFilePath);
        } catch (Exception e) {
            if (videoPath != null) {
                log.error(e.getMessage());
                cleanFile(videoPath);
            }
            if (coverPath != null) {
                log.error(e.getMessage());
                cleanFile(coverPath);
            }
            redisTemplate.opsForValue().set("video:task:" + taskId,
                    JSON.toJSONString(AsyncTaskResult.fail("上传失败" + e.getMessage(), taskId)),
                    3600, TimeUnit.SECONDS);
            log.error("视频投稿异步任务执行失败 | taskId: {} | userId: {} | 视频路径: {} | 封面路径: {}",
                    taskId, userId, videoPath, coverPath, e);
        }
    }

    @Override
    public String postVideo(String filePath) {
        // todo 文件上传可以优化
        String url = UUID.randomUUID() + ".mp4";
        Path targetPath = null;
        try {
            Path sourcePath = Paths.get(filePath); // 源文件Path
            targetPath = Paths.get(basePath, VIDEO_FOLDER, url); // 目标文件Path（推荐拼接方式）
            Path parentDir = targetPath.getParent();

            // 2. 关键：递归创建父目录（如果已存在，不会报错）
            if (parentDir != null) { // 防止父路径为 null（比如 targetPath 是根路径）
                Files.createDirectories(parentDir);
                System.out.println("父目录创建成功：" + parentDir);
            } else {
                System.out.println("targetPath 无父目录（根路径），无需创建");
            }
            Files.copy(
                    sourcePath,
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING // 覆盖已存在的文件（可选，根据需求调整）
            );
            return targetPath.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
            cleanFile(targetPath.toString());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public String postVideoCover(String coverPath) {
        String url = UUID.randomUUID() + ".png";
        Path targetPath = null;
        try {
            Path sourcePath = Paths.get(coverPath); // 源文件Path
            targetPath = Paths.get(basePath, COVER_FOLDER, url); // 目标文件Path（推荐拼接方式）
            Path parentDir = targetPath.getParent();

            // 2. 关键：递归创建父目录（如果已存在，不会报错）
            if (parentDir != null) { // 防止父路径为 null（比如 targetPath 是根路径）
                Files.createDirectories(parentDir);
                System.out.println("父目录创建成功：" + parentDir);
            } else {
                System.out.println("targetPath 无父目录（根路径），无需创建");
            }
            Files.copy(
                    sourcePath,
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING // 覆盖已存在的文件（可选，根据需求调整）
            );
            return targetPath.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
            cleanFile(targetPath.toString());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void insertVideo(Video video) {
        int insert = videoDao.insert(video);
        log.info("{}视频插入数据库成功", video.getId());
        if (insert <= 0) {
            log.info("{}视频插入数据库失败", video.getId());
            throw new SystemException("视频插入失败");
        }
    }

    @Override
    public IPage<Video> selectVideoByPage(String id, int page, int size) {
        IPage objectPage = new Page<>(page, size);
        QueryWrapper<Video> wrapper = new QueryWrapper<Video>().eq("user_id", id);
        videoDao.selectPage(objectPage, wrapper);
        return objectPage;
    }

    private void cleanFile(String path) {
        if (path == null) {
            log.info("路径是空的");
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        } else {
            log.info("清理失败投稿的封面文件 | 路径: {}", path);
            throw new BusinessException(path + "该文件找不到");
        }
    }

    /**
     * 搜索视频
     * @param videoSearchDTO
     * @param currentUserId
     * @return
     */
    public POJOList<Video> searchVideo(@Validated VideoSearchDTO videoSearchDTO, String currentUserId) {
        String keywords = videoSearchDTO.getKeywords();
        Long fromDate = videoSearchDTO.getFromDate();
        Long toDate = videoSearchDTO.getToDate();
        Integer pageNum = videoSearchDTO.getPageNum();
        Integer pageSize = videoSearchDTO.getPageSize();

        String username = videoSearchDTO.getUsername();
        if (keywords == null) {
            throw new BusinessException("未传入keywords");
        }
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        if (!keywords.equals("")) {
            videoQueryWrapper.and(wrapper -> wrapper.like("title", keywords)
                    .or()
                    .like("description", keywords));
        }
        if (fromDate != null && toDate != null) {
            System.out.println(fromDate);
            Instant fromDateInstant = Instant.ofEpochMilli(fromDate);
            Instant toDateInstant = Instant.ofEpochMilli(toDate);
            LocalDateTime fromDateTime = LocalDateTime.ofInstant(fromDateInstant, ZoneId.systemDefault());
            LocalDateTime toDateTime = LocalDateTime.ofInstant(toDateInstant, ZoneId.systemDefault());
            System.out.println("fromDate:" + fromDateTime);
            System.out.println("toDate:" + toDateTime);
            videoQueryWrapper.between("created_at", fromDateTime, toDateTime);
        }
        if (username != null && !username.trim().isEmpty()) {
            List<User> users = userDao.selectList(new QueryWrapper<User>().like("username", username));
            if (CollectionUtils.isEmpty(users)) {
                throw new BusinessException("当前搜索用户不存在");
            }
            List<String> userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            // 4. 用 in 条件筛选视频的 user_id
            videoQueryWrapper.in("user_id", userIds);
        }
        pageSize = PageUtil.getPageSize(pageSize);
        pageNum = PageUtil.getPageNum(pageNum);
        Page page = new Page<>(pageNum, pageSize);
        Page page1 = videoDao.selectPage(page, videoQueryWrapper);
        List videoList = page1.getRecords();
        long total = page1.getTotal();
        saveSearchHistory(videoSearchDTO, currentUserId);
        return new POJOList<Video>(videoList, total);
    }

    @Override
    public POJOList<Video> getAndSetRank() {
        return null;
    }

    public List<Video> getVideoList(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        int batchSize = 500; // 每批500个ID，MySQL推荐阈值
        List<Video> allVideos = new ArrayList<>();
        int size = ids.size();
        for (int i = 0; i < size; i += batchSize) {
            // 截取当前批次ID：[i, min(i+batchSize, 总长度))
            List<String> batchIds = ids.subList(i, Math.min(i + batchSize, size));
            // MP 内置的批量查询（selectBatchIds）
            List<Video> batchVideos = videoDao.selectBatchIds(batchIds);
            allVideos.addAll(batchVideos);
        }
        // 4. 保证返回顺序与传入ids一致（调用方体验最优）
        Map<String, Video> videoMap = allVideos.stream()
                .collect(Collectors.toMap(
                        Video::getId,          // key=视频ID
                        video -> video,        // value=视频对象
                        (v1, v2) -> v1         // 重复ID保留第一个（理论上不会出现）
                ));
        // 按原始传入顺序组装，过滤掉不存在的视频（可选保留null）
        List<Video> orderedVideos = ids.stream()
                .map(videoMap::get)
                .filter(Objects::nonNull) // 过滤数据库中不存在的ID
                .collect(Collectors.toList());
        return orderedVideos;
    }

    /**
     * 排行榜
     * 通过从redis中获取点击量最高的视频id和点击量，再到mysql获取到完整的视频信息，最后封装为POJOLit返回
     *
     * @param pageQueryDTO
     * @return POJOList<Video>
     */
    @Override
    public POJOList<Video> rankingList(@Validated PageQueryDTO pageQueryDTO) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Integer pageNum = pageQueryDTO.getPage_num();
        Integer pageSize = pageQueryDTO.getPage_size();
        // range: start=起始偏移, end=结束偏移；
        Integer start = PageUtil.getPageNum(pageNum, pageSize);
        Integer end = pageSize + pageNum - 1;
        Set<ZSetOperations.TypedTuple<String>> videoSet = zSetOperations.reverseRangeWithScores(
                VIDEO_VISIT_RANK_KEY, start, end
        );

        if (CollectionUtil.isEmpty(videoSet)) {
            return new POJOList<>(Collections.emptyList(), null);
        }

        List<String> videoIds = new ArrayList<>(videoSet.size());
        Map<String, Double> videoVisitCountMap = new HashMap<>(videoSet.size());

        for (ZSetOperations.TypedTuple<String> tuple : videoSet) {
            Object valueObj = tuple.getValue();
            Double score = tuple.getScore();
            if (valueObj == null || score == null) {
                continue;
            }
            String videoId = String.valueOf(valueObj);
            videoIds.add(videoId);
            videoVisitCountMap.put(videoId, score);
        }

        if (CollectionUtil.isEmpty(videoIds)) {
            return new POJOList<>(Collections.emptyList(), null);
        }

        List<Video> videos = videoDao.selectVideoListById(videoIds);
        List<Video> finalVideos = videos.stream()
                .peek(video -> {
                    Double realVisitCount = videoVisitCountMap.get(video.getId());
                    if (realVisitCount != null) {
                        video.setVisitCount(realVisitCount.intValue());
                    } else {
                        video.setVisitCount(0);
                    }
                })
                .collect(Collectors.toList());
        return new POJOList<>(finalVideos, null);
    }

    /**
     * 写入「ZSet（索引） + Hash（详情）」
     *
     * @param videoSearchDTO
     */
    public void saveSearchHistory(VideoSearchDTO videoSearchDTO, String userId) {
        String keywords = videoSearchDTO.getKeywords();
        Long fromDate = videoSearchDTO.getFromDate();
        Long toDate = videoSearchDTO.getToDate();
        String username = videoSearchDTO.getUsername();
        // 1. 生成「搜索时间戳」（作为ZSet的Score）
        long searchTime = LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Shanghai"))
                .toInstant()
                .toEpochMilli();

        // 2. 定义「ZSet的Key」（示例：search:history:1001）
        String zSetKey = "search:history:" + userId;


        // 3. 定义「ZSet的Member」（示例：search:1001:1740004800000）
        String zSetMember = "search:" + userId + ":" + searchTime;

        // 4. 把Member和Score写入ZSet
        redisTemplate.opsForZSet().add(zSetKey, zSetMember, searchTime);
        // （可选）限制ZSet最多存100条记录，超出则删除最早的
        redisTemplate.opsForZSet().removeRange(zSetKey, 0, -101);


        // 5. 把搜索参数写入「Hash」（Hash的Key就是ZSet的Member）
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("keywords", keywords);
        searchParams.put("searchTime", searchTime); // 搜索时间戳
        searchParams.put("fromDate", fromDate);
        searchParams.put("toDate", toDate);
        redisTemplate.opsForHash().putAll(zSetMember, searchParams);


        // 6. （可选）设置Hash和ZSet的过期时间（比如90天）
        redisTemplate.expire(zSetKey, 90, TimeUnit.DAYS);
        redisTemplate.expire(zSetMember, 90, TimeUnit.DAYS);
    }

    @Override
    public List<Video> getVideos(String latestTime, String userId) {
        List<Video> videos = videoDao.selectHomePageVideo(latestTime, homepageVideoSize);
        log.info("{} 获取首页视频成功", userId);
        return videos;
    }

    @Override
    public void updateVideoStatus(String videoId, VideoStatus videoStatus) {
        videoDao.updateVideoStatus(videoId, videoStatus);
        log.info("{} 更新视频状态成功 | videoId: {} | videoStatus: {}", videoId, videoId, videoStatus);
    }
}
