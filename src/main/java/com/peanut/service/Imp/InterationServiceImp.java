package com.peanut.service.Imp;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peanut.POJO.DTO.LikesDTO;
import com.peanut.POJO.entity.*;
import com.peanut.dao.*;
import com.peanut.expection.BusinessException;
import com.peanut.interaction.dao.InterationDao;
import com.peanut.service.InterationService;
import com.peanut.video.eneity.pojo.Video;
import com.peanut.video.dao.VideoDao;
import com.peanut.video.service.VideoService;
import com.peanut.utils.IdUtil;
import com.peanut.utils.PageUtil;
import com.peanut.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
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
    InterationDao likesDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Resource(name = "streamRedisTemplate")
    private RedisTemplate<String, String> streamRedisTemplate;

    private static final Logger log = LoggerFactory.getLogger(InterationServiceImp.class);

    @Override
    public void like(LikesDTO likesDTO, String userId) {
        int type = likesDTO.getType();
        String targetId = likesDTO.getTargetId();
        int status = likesDTO.getStatus(); // 1=like, 0=unlike

        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }

        // 目标合法性校验（视频/评论/帖子是否存在等）
        validateType(targetId, type);

        // Redis keys（
        String targetLikeKey = "like:target:" + type + ":" + targetId;  // Set: member=userId
        String userLikeKey = "like:user:" + userId + ":" + type;        // Set: member=targetId
        String eventsKey = "like:events";                                // Stream
        String rankKey = "like:rank:" + type;                            // ZSet: member=targetId score=count

        // Lua：维护双向Set + 排行榜 + 事件流（原子、幂等）
        String luaScript = """
        local userId = ARGV[1]
        local targetId = ARGV[2]
        local type = ARGV[3]
        local op = ARGV[4]
        local time = ARGV[5]

        if tonumber(op) == 1 then
          local added = redis.call("SADD", KEYS[1], userId)
          if added == 1 then
            redis.call("ZADD", KEYS[2], time, targetId)
            redis.call("ZINCRBY", KEYS[4], 1, targetId)
            redis.call("XADD", KEYS[3], "*",
              "op","like","userId",userId,"type",type,"targetId",targetId)
            return 1
          else
            return 0
          end
        else
          -- unlike: only if liked before
          local removed = redis.call("SREM", KEYS[1], userId)
          if removed == 1 then
            redis.call("ZREM", KEYS[2], targetId)
            redis.call("ZINCRBY", KEYS[4], -1, targetId)
            redis.call("XADD", KEYS[3], "*",
              "op","unlike","userId",userId,"type",type,"targetId",targetId)
            return 1
          else
            return 0
          end
        end
        """;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);

        List<String> keys = Arrays.asList(
                targetLikeKey, // KEYS[1]
                userLikeKey,   // KEYS[2]
                eventsKey,     // KEYS[3]
                rankKey        // KEYS[4]
        );

        Long time = System.currentTimeMillis();

        Long result = streamRedisTemplate.execute(
                script,
                keys,
                userId,
                targetId,
                String.valueOf(type),
                String.valueOf(status),
                String.valueOf(time)
        );

        if (result == null) {
            throw new BusinessException("点赞操作失败，请稍后重试");
        }

        if (result == 0) {
            // 幂等：重复点赞/重复取消
            throw new BusinessException(status == 1
                    ? "您已点赞该目标，无需重复操作"
                    : "您已取消点赞该目标，无需重复操作");
        }
    }

    private void validateType(String toTargetId, int type) {
        switch (type) {
            case 1:
                if (videoDao.selectById(toTargetId) == null) {
                    throw new BusinessException("视频不存在");
                }
                break;
            case 2:
                if (commentDao.selectById(toTargetId) == null) {
                    throw new BusinessException("评论不存在");
                }
                break;
            default:
                throw new IllegalArgumentException("无效的点赞类型");
        }
    }

    public POJOList<Video> getLikeVideos(String userId, int pageNum, int pageSize) {
        User user = userDao.selectById(userId);
        if (user == null) return new POJOList<>();

        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 50) pageSize = 20;

        String key = "like:user:" + userId + ":1";
        System.out.println(key
        );
        long start = (long) (pageNum - 1) * pageSize;
        long end = start + pageSize - 1;

        // 直接 Redis 分页取
        Set<String> idSet = stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
        if (idSet == null || idSet.isEmpty()) return new POJOList<>();

        List<String> pageIds = new ArrayList<>(idSet);

        // 批量查视频（建议保持返回顺序与pageIds一致）
        List<Video> videoList = videoService.getVideoList(pageIds);

        return new POJOList<>(videoList, null);
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
    public void likesToMysql() {
        log.info("将点赞事件从Redis Stream写到MySQL {}", DateTime.now());
        String streamKey = RedisLikeKeys.LIKE_EVENTS_STREAM;
        String group = RedisLikeKeys.LIKE_EVENTS_GROUP;
        String consumer = "like-consumer-1"; // 可以根据实际情况动态生成或配置

        int batchSize = 200;

        List<MapRecord<String, Object, Object>> records = streamRedisTemplate.opsForStream().read(
                Consumer.from(group, consumer),
                StreamReadOptions.empty().count(batchSize).block(Duration.ofSeconds(2)),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed())
        );

        if (records == null || records.isEmpty()) {
            System.out.println("没有待处理的点赞事件");
            return;
        }

        List<RecordId> ackIds = new ArrayList<>(records.size());
        // 关键：本批要写入 MySQL 的集合
        List<Likes> batch = new ArrayList<>(records.size());

        LocalDateTime now = LocalDateTime.now();
        try {
            for (MapRecord<String, Object, Object> record : records) {
                Map<Object, Object> m = record.getValue();

                String op = Objects.toString(m.get("op"), "");
                String userId = Objects.toString(m.get("userId"), "");
                String targetId = Objects.toString(m.get("targetId"), "");
                int type = Integer.parseInt(Objects.toString(m.get("type"), "0"));

                if (!org.springframework.util.StringUtils.hasText(op)
                        || !org.springframework.util.StringUtils.hasText(userId)
                        || !org.springframework.util.StringUtils.hasText(targetId)
                        || type <= 0) {
                    // 脏数据也 ack，避免卡 pending
                    ackIds.add(record.getId());
                    continue;
                }

                Integer status;
                if ("like".equals(op)) {
                    status = 1;
                } else if ("unlike".equals(op)) {
                    status = 0;
                } else {
                    // 未知 op：直接 ack
                    ackIds.add(record.getId());
                    continue;
                }

                Likes like = new Likes();
                like.setId(IdUtil.getId()); // varchar(32)
                like.setUserId(userId);
                like.setType(type);
                like.setTargetId(targetId);
                like.setStatus(status);
                like.setCreatedAt(now);
                like.setUpdatedAt(now);

                batch.add(like);
                ackIds.add(record.getId());
            }

            // 批量写库：只要这一步成功，才 ack
            if (!batch.isEmpty()) {
                likesDao.batchInsert(batch);
            }


            if (!ackIds.isEmpty()) {
                redisTemplate.opsForStream()
                        .acknowledge(streamKey, group, ackIds.toArray(new RecordId[0]));
            }

            redisTemplate.opsForStream().trim(streamKey, 1_000_000, true);
        } catch (Exception e) {
            log.error("消费点赞Stream写入MySQL失败，将回滚事务，消息不ACK以便重试", e);
            throw e;
        }
    }

}
