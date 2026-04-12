package com.peanut.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peanut.POJO.entity.*;
import com.peanut.dao.*;
import com.peanut.POJO.DTO.LikeCountIncrementDTO;
import com.peanut.expection.BusinessException;
import com.peanut.service.InterationService;
import com.peanut.service.VideoService;
import com.peanut.utils.PageUtil;
import com.peanut.utils.RedisUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InterationServiceImp implements InterationService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    UserDao userDao;

    @Autowired
    VideoDao videoDao;

    @Autowired
    VideoService videoService;

    @Autowired
    CommentDao commentDao;

    @Autowired
    VideoLikeDao videoLikeDao;

    @Autowired
    CommentLikeDao commentLikeDao;

    private static final Logger log = LoggerFactory.getLogger(InterationServiceImp.class);

    @Override
    public void likeVideo(String userId, String videoId, Integer isLike) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(videoId)) {
            throw new IllegalArgumentException("用户ID/视频ID不能为空");
        }

        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }

        Video video = videoDao.selectById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }

        String key = RedisUtil.getKey(userId, videoId);

        String luaScript = """
                   local current = redis.call('HGET', KEYS[1], KEYS[2])
                   current = tonumber(current) or 0
                   local target = tonumber(ARGV[1]) or 0
                   if current == target then
                       return 0
                   else
                       redis.call('HSET', KEYS[1], KEYS[2], target)
                       return 1
                   end
                """;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);
        // 修正Lua脚本的KEYS/ARGV传递（上面的List传两个Key）
        List<String> keys = new ArrayList<>();
        keys.add(RedisUtil.VIDEO_LIKE);
        keys.add(key);

        // 8. 执行Lua脚本（核心：参数正确传递，返回值类型匹配）
        Long result = redisTemplate.execute(script, keys, isLike);

        // 防御性判空
        if (result == null) {
            throw new BusinessException("点赞操作失败，请稍后重试");
        }

        // 结果处理（逻辑无问题，保留）
        if (result == 0) {
            throw new BusinessException(isLike == 1 ? "您已点赞该视频，无需重复操作" : "您已取消点赞该视频，无需重复操作");
        }
    }

    public POJOList<Video> getLikeVideos(String userId, int pageNum, int pageSize) {
        User user = userDao.selectById(userId);
        if (user == null) {
            return null;
        }
        String prefix = userId + "::";
        // 初始化scan游标（0表示开始）
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(prefix + "*") // 模糊匹配：只扫描以"userId:"开头的字段（减少遍历量）
                .count(100) // 每次扫描100条（可根据数据量调整）
                .build();
        Set<Object> allFields = redisTemplate.opsForHash().keys(RedisUtil.VIDEO_LIKE);
        // 分页扫描Hash字段
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(RedisUtil.VIDEO_LIKE, scanOptions);
        List<String> videoIds = new ArrayList<>();
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            String field = String.valueOf(entry.getKey());
            // 拆分videoId（此时field已匹配前缀，直接截取即可）
            String videoId = field.substring(prefix.length());
            if ((int) entry.getValue() != 1) continue;
            videoIds.add(videoId);
        }
        if (pageNum < 1) {
            pageNum = 1; // 页码<1 强制改为第1页
        }
        if (pageSize < 1 || pageSize > 50) {
            pageSize = 20; // 限制单页最大条数，避免超大页
        }

        // 2. 空列表直接返回（避免后续计算）
        if (CollectionUtils.isEmpty(videoIds)) {
            return new POJOList<>();
        }

        // 3. 计算分页索引，防护越界
        int total = videoIds.size();
        int start = (pageNum - 1) * pageSize;
        // 核心防护：起始索引超出总条数 → 返回空列表
        if (start >= total) {
            return new POJOList<>();
        }
        // 计算结束索引，确保 end >= start 且不超过总条数
        int end = Math.min(start + pageSize, total);
        end = Math.max(end, start); // 兜底：避免 start > end
        List<String> pageIds = videoIds.subList(start, end);

        List<Video> videoList = videoService.getVideoList(pageIds);

        // 关闭游标，避免资源泄漏
        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new POJOList<>(videoList, null);
    }

    @Override
    public void likeComment(String userId, String commentId, Integer isLike) {
        //todo 1. 对于错误的判别，2.并发问题 3. 重复代码的封装 4. 索引问题 5. 获取知识渠道，判断正确问题 6. 可以用redis缓存 7.日志标记检验注解位置 8. 线程池
        if (userId == null || commentId == null) {
            throw new IllegalArgumentException("用户ID/评论ID不能为空");
        }
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        Comment comment = commentDao.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        String key = RedisUtil.getKey(userId, commentId);

        String luaScript = """
                   local current = redis.call('HGET', KEYS[1], KEYS[2])
                   current = tonumber(current) or 0
                   local target = tonumber(ARGV[1]) or 0
                   if current == target then
                       return 0
                   else
                       redis.call('HSET', KEYS[1], KEYS[2], target)
                       return 1
                   end
                """;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);
        // 修正Lua脚本的KEYS/ARGV传递（上面的List传两个Key）
        List<String> keys = new ArrayList<>();
        keys.add(RedisUtil.COMMENT_LIKE);
        keys.add(key);

        // 8. 执行Lua脚本（核心：参数正确传递，返回值类型匹配）
        Long result = redisTemplate.execute(script, keys, isLike);

        // 防御性判空
        if (result == null) {
            throw new BusinessException("点赞操作失败，请稍后重试");
        }

        if (result == 0) {
            throw new BusinessException(isLike == 1 ? "您已点赞该评论，无需重复操作" : "您已取消点赞该评论，无需重复操作");
        }
    }

    public List<Video> getLikeComments(String userId, int pageNum, int pageSize) {
        User user = userDao.selectById(userId);
        if (user == null) {
            return null;
        }
        String prefix = userId + "::";
        // 初始化scan游标（0表示开始）
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(prefix + "*") // 模糊匹配：只扫描以"userId:"开头的字段（减少遍历量）
                .count(100) // 每次扫描100条（可根据数据量调整）
                .build();
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(RedisUtil.COMMENT_LIKE, scanOptions);
        List<String> commentIds = new ArrayList<>();
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            String field = String.valueOf(entry.getKey());
            // 拆分videoId（此时field已匹配前缀，直接截取即可）
            String commentId = field.substring(prefix.length());
            if ((int) entry.getValue() != 1) continue;
            commentIds.add(commentId);
        }
        if (pageNum < 1) {
            pageNum = 1; // 页码<1 强制改为第1页
        }
        if (pageSize < 1 || pageSize > 50) {
            pageSize = 20; // 限制单页最大条数，避免超大页
        }

        // 2. 空列表直接返回（避免后续计算）
        if (CollectionUtils.isEmpty(commentIds)) {
            return new ArrayList<>();
        }

        // 3. 计算分页索引，防护越界
        int total = commentIds.size();
        int start = (pageNum - 1) * pageSize;
        // 核心防护：起始索引超出总条数 → 返回空列表
        if (start >= total) {
            return new ArrayList<>();
        }
        // 计算结束索引，确保 end >= start 且不超过总条数
        int end = Math.min(start + pageSize, total);
        end = Math.max(end, start); // 兜底：避免 start > end
        List<String> pageIds = commentIds.subList(start, end);

        List<Video> videoList = videoService.getVideoList(pageIds);

        // 关闭游标，避免资源泄漏
        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoList;
    }

    @Override
    public void comment(String userId, String videoId, String commentId, String content) {
        if (userId == null) {
            throw new BusinessException("用户id不存在");
        }
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (videoId == null && commentId == null) {
            throw new BusinessException("请给出评论对象");
        }
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment(0, content, now, null, null, 0, null, now, userId, null);
        if (commentId != null) {
            Comment parentComment = commentDao.selectById(commentId);
            if (parentComment == null) {
                throw new BusinessException("评论id有误");
            }
            comment.setVideoid(parentComment.getVideoid());
            comment.setParentid(commentId);
        }
        if (videoId != null) {
            if (videoDao.selectById(videoId) == null) {
                throw new BusinessException("视频id有误");
            }
            comment.setVideoid(videoId);
        }
        int insert = commentDao.insert(comment);
        if (insert == 0) {
            throw new BusinessException("插入失败");
        }
    }

    @Override
    public List<Comment> getCommentList(String videoId, String commentId, int pageNum, int pageSize) {
        if (videoId == null && commentId == null) {
            throw new BusinessException("请给出评论对象");
        }
        pageNum = PageUtil.getPageNum(pageNum);
        pageSize = PageUtil.getPageSize(pageSize);
        IPage<Comment> commentPage = new Page<>(pageNum, pageSize);
        if (videoId != null) {
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<Comment>().eq("video_id", videoId);
            IPage iPage1 = commentDao.selectPage(commentPage, commentQueryWrapper);
            return iPage1.getRecords();
        } else {
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<Comment>().eq("parent_id", commentId);
            IPage iPage1 = commentDao.selectPage(commentPage, commentQueryWrapper);
            return iPage1.getRecords();
        }
    }

    @Override
    public void deleteComment(String userId, String videoId, String commentId) {
        if (userId == null || videoId == null || commentId == null) {
            throw new BusinessException("未传入关键参数");
        }
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Video video = videoDao.selectById(videoId);
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        Comment comment = commentDao.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (comment.getIsDeleted() == 1) {
            throw new BusinessException("评论已删除");
        }
        comment.setIsDeleted(1);
        comment.setDeletedAt(LocalDateTime.now());
        if (video.getUserid().equals(userId)) {
            if (comment.getVideoid().equals(videoId)) {
                int i = commentDao.updateById(comment);
                if (i == 0) {
                    throw new BusinessException("更新失败");
                }
            } else {
                throw new BusinessException("评论的视频不是该视频，不能删除");
            }
        } else {
            if (comment.getVideoid().equals(videoId) && comment.getUserid().equals(userId)) {
                int i = commentDao.updateById(comment);
                if (i == 0) {
                    throw new BusinessException("更新失败");
                }
            } else {
                throw new BusinessException("评论的视频不是该视频或是不是该id的主人，不能删除");
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeToMysql() {
        log.info("将视频点赞从redis写到mysql");
        String redisLikeKey = RedisUtil.VIDEO_LIKE;
        // 1. 使用游标分批扫描 Redis Hash
        Cursor<Map.Entry<Object, Object>> cursor = null;

        List<Set<Object>> batchKeysToDelete = new ArrayList<>();
        try {
            cursor = redisTemplate.opsForHash().scan(redisLikeKey, ScanOptions.scanOptions()
                    .count(1000) // 每批获取1000条
                    .build());

            while (cursor.hasNext()) {
                // 2. 解析 Redis 数据，转换为数据库可操作的实体/参数列表
                List<VideoLike> likeList = new ArrayList<>();
                Map<String, Integer> videoLikeCountMap = new HashMap<>();
                Set<Object> keysToDelete = new HashSet<>();
                // 收集当前批次的数据（最多1000条）
                int batchSize = 0;
                while (cursor.hasNext() && batchSize < 1000) {
                    Map.Entry<Object, Object> entry = cursor.next();
                    // 遍历Redis Hash中的所有键值对（FIELD=userId_videoId，VALUE=isLike）
                    // 解析 FIELD：userId_videoId -> 拆分出用户ID和视频ID
                    String field = (String) entry.getKey();
                    String[] idArr = field.split("::"); // 与 RedisUtil.getKey(userId, videoId) 拆分规则一致
                    if (idArr.length != 2) {
                        continue; // 无效格式，跳过
                    }
                    String userId = idArr[0];
                    String videoId = idArr[1];
                    Integer isLike = (Integer) entry.getValue() == 1 ? 0 : 1; // 解析 VALUE：点赞状态（1=点赞，0=取消点赞）
                    // 封装为数据库实体
                    VideoLike videoLike = new VideoLike();
                    videoLike.setUserId(userId);
                    videoLike.setVideoId(videoId);
                    videoLike.setCancel(isLike);
                    likeList.add(videoLike);
                    keysToDelete.add(field);
                    batchSize++;
                }
                // 处理当前批次
                if (!likeList.isEmpty()) {
                    batchKeysToDelete.add(keysToDelete);
                    processBatch(likeList, videoLikeCountMap, keysToDelete);
                    // 每批处理完后提交事务，避免大事务
                    TransactionAspectSupport.currentTransactionStatus().flush();
                }
            }
            // 所有批次MySQL操作成功后，批量删除Redis key（核心：先确认MySQL成功，再删Redis）
            for (Set<Object> keys : batchKeysToDelete) {
                if (!keys.isEmpty()) {
                    redisTemplate.opsForHash().delete(redisLikeKey, keys.toArray());
                }
            }
        } catch (Exception e) {
            log.error("同步点赞数据到MySQL失败，已触发回滚", e);
            throw new RuntimeException("同步点赞数据失败", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * 处理单个批次的数据
     */
    private void processBatch(List<VideoLike> likeList,
                              Map<String, Integer> videoLikeCountMap,
                              Set<Object> keysToDelete) {
        // 提取数据库已有记录的唯一标识（userId::videoId），用于快速判断冲突
        Set<String> existLikeKeySet = new HashSet<>();
        List<VideoLike> originalLikes = videoLikeDao.batchQueryByUserAndVideo(likeList);
        for (VideoLike original : originalLikes) {
            existLikeKeySet.add(original.getUserId() + "::" + original.getVideoId());
        }

        for (VideoLike current : likeList) {
            String uniqueKey = current.getUserId() + "::" + current.getVideoId();
            String videoId = current.getVideoId();
            Integer isCancel = current.isCancel();

            // 初始化该视频的点赞数增量（默认0）
            int increment = videoLikeCountMap.getOrDefault(videoId, 0);

            if (existLikeKeySet.contains(uniqueKey)) {
                // 场景1：数据库已有记录（冲突）
                if (isCancel == 0) { // Redis中是点赞 → 加1
                    increment++;
                } else if (isCancel == 1) { // Redis中是取消点赞 → 减1
                    increment--;
                }
            } else {
                // 场景2：数据库无记录（新增）
                if (isCancel == 0) { // 新增点赞 → 加1
                    increment++;
                }
                // 新增取消点赞 → 不处理（increment保持0）
            }

            // 更新该视频的点赞数增量
            videoLikeCountMap.put(videoId, increment);
        }


        // 3. 批量同步到 MySQL 数据库（分两种场景：新增/更新 点赞关系、更新视频总点赞数）
        if (!likeList.isEmpty()) {
            // 3.1 批量处理用户-视频点赞关系（按需选择：批量新增或批量更新）
            videoLikeDao.batchSaveOrUpdateVideoLike(likeList);
            if (!videoLikeCountMap.isEmpty()) {
                List<LikeCountIncrementDTO> incrementList = new ArrayList<>();
                // 遍历视频点赞数字典，批量更新视频表的点赞数字段
                for (Map.Entry<String, Integer> entry : videoLikeCountMap.entrySet()) {
                    LikeCountIncrementDTO dto = new LikeCountIncrementDTO();
                    dto.setId(entry.getKey());
                    dto.setTotalIncrement(entry.getValue());
                    incrementList.add(dto);
                }
                // 假设视频表实体为Video，点赞数字段为likeCount，此处为更新逻辑
                videoDao.batchUpdateVideoLikeCount(incrementList);
            }
        }


        // 4. 同步完成后，清空 Redis 中已同步的点赞数据（避免重复同步）
        if (!keysToDelete.isEmpty()) {
            redisTemplate.opsForHash().delete(RedisUtil.VIDEO_LIKE, keysToDelete.toArray());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commentLikeToMysql() {
        log.info("将评论点赞从redis写到mysql");
        String redisLikeKey = RedisUtil.COMMENT_LIKE;
        // 1. 使用游标分批扫描 Redis Hash
        Cursor<Map.Entry<Object, Object>> cursor = null;

        List<Set<Object>> batchKeysToDelete = new ArrayList<>();
        try {
            cursor = redisTemplate.opsForHash().scan(redisLikeKey, ScanOptions.scanOptions()
                    .count(1000) // 每批获取1000条
                    .build());

            while (cursor.hasNext()) {
                // 2. 解析 Redis 数据，转换为数据库可操作的实体/参数列表
                List<CommentLike> likeList = new ArrayList<>();
                Map<String, Integer> videoLikeCountMap = new HashMap<>();
                Set<Object> keysToDelete = new HashSet<>();
                // 收集当前批次的数据（最多1000条）
                int batchSize = 0;
                while (cursor.hasNext() && batchSize < 1000) {
                    Map.Entry<Object, Object> entry = cursor.next();
                    // 遍历Redis Hash中的所有键值对（FIELD=userId_commentId，VALUE=isLike）
                    // 解析 FIELD：userId_commentId -> 拆分出用户ID和评论ID
                    String field = (String) entry.getKey();
                    String[] idArr = field.split("::"); // 与 RedisUtil.getKey(userId, commentId) 拆分规则一致
                    if (idArr.length != 2) {
                        continue; // 无效格式，跳过
                    }
                    String userId = idArr[0];
                    String commentId = idArr[1];
                    Integer isLike = (Integer) entry.getValue() == 1 ? 0 : 1; // 解析 VALUE：点赞状态（1=点赞，0=取消点赞）
                    // 封装为数据库实体
                    CommentLike commentLike = new CommentLike();
                    commentLike.setId(IdWorker.getIdStr());
                    commentLike.setUserId(userId);
                    commentLike.setCommentId(commentId);
                    commentLike.setIsCancel(isLike);
                    keysToDelete.add(field);
                    likeList.add(commentLike);
                    batchSize++;
                }
                // 处理当前批次
                if (!likeList.isEmpty()) {
                    batchKeysToDelete.add(keysToDelete);
                    processCommentLikeBatch(likeList, videoLikeCountMap, keysToDelete);
                    // 每批处理完后提交事务，避免大事务
                    TransactionAspectSupport.currentTransactionStatus().flush();
                }
            }
            // 所有批次MySQL操作成功后，批量删除Redis key（核心：先确认MySQL成功，再删Redis）
            for (Set<Object> keys : batchKeysToDelete) {
                if (!keys.isEmpty()) {
                    redisTemplate.opsForHash().delete(redisLikeKey, keys.toArray());
                }
            }
        } catch (Exception e) {
            log.error("同步点赞数据到MySQL失败，已触发回滚", e);
            throw new RuntimeException("同步点赞数据失败", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * 处理单个批次的数据
     */
    private void processCommentLikeBatch(List<CommentLike> commentLikes,
                              Map<String, Integer> commentLikeCountMap,
                              Set<Object> keysToDelete) {
        // 提取数据库已有记录的唯一标识（userId::commentId），用于快速判断冲突
        Set<String> existLikeKeySet = new HashSet<>();
        List<CommentLike> originalLikes = commentLikeDao.batchQueryByUserAndComment(commentLikes);
        for (CommentLike original : originalLikes) {
            existLikeKeySet.add(original.getUserId() + "::" + original.getCommentId());
        }

        for (CommentLike current : commentLikes) {
            String uniqueKey = current.getUserId() + "::" + current.getCommentId();
            String commentId = current.getCommentId();
            Integer isCancel = current.getIsCancel();

            // 初始化该评论的点赞数增量（默认0）
            int increment = commentLikeCountMap.getOrDefault(commentId, 0);

            if (existLikeKeySet.contains(uniqueKey)) {
                // 场景1：数据库已有记录（冲突）
                if (isCancel == 0) { // Redis中是点赞 → 加1
                    increment++;
                } else if (isCancel == 1) { // Redis中是取消点赞 → 减1
                    increment--;
                }
            } else {
                // 场景2：数据库无记录（新增）
                if (isCancel == 0) { // 新增点赞 → 加1
                    increment++;
                }
                // 新增取消点赞 → 不处理（increment保持0）
            }

            // 更新该评论的点赞数增量
            commentLikeCountMap.put(commentId, increment);
        }


        // 3. 批量同步到 MySQL 数据库（分两种场景：新增/更新 点赞关系、更新视频总点赞数）
        if (!commentLikes.isEmpty()) {
            // 3.1 批量处理用户-视频点赞关系（按需选择：批量新增或批量更新）
            commentLikeDao.batchSaveOrUpdateCommentLike(commentLikes);
            if (!commentLikeCountMap.isEmpty()) {
                List<LikeCountIncrementDTO> incrementList = new ArrayList<>();
                // 遍历视频点赞数字典，批量更新视频表的点赞数字段
                for (Map.Entry<String, Integer> entry : commentLikeCountMap.entrySet()) {
                    LikeCountIncrementDTO dto = new LikeCountIncrementDTO();
                    dto.setId(entry.getKey());
                    dto.setTotalIncrement(entry.getValue());
                    incrementList.add(dto);
                }
                commentDao.batchUpdateCommentLikeCount(incrementList);
            }
        }


        // 4. 同步完成后，清空 Redis 中已同步的点赞数据（避免重复同步）
        if (!keysToDelete.isEmpty()) {
            redisTemplate.opsForHash().delete(RedisUtil.COMMENT_LIKE, keysToDelete.toArray());
        }
    }
}
