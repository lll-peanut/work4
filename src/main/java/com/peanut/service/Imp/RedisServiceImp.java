package com.peanut.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peanut.Dao.CommentDao;
import com.peanut.Dao.UserDao;
import com.peanut.Dao.VideoDao;
import com.peanut.POJO.Comment;
import com.peanut.POJO.POJOList;
import com.peanut.POJO.User;
import com.peanut.POJO.Video;
import com.peanut.expection.BusinessException;
import com.peanut.service.RedisService;
import com.peanut.service.VideoService;
import com.peanut.utils.PageUtil;
import com.peanut.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RedisServiceImp implements RedisService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserDao userDao;

    @Autowired
    VideoDao videoDao;

    @Autowired
    VideoService videoService;

    @Autowired
    CommentDao commentDao;

    @Override
    public void likeVideo(String userId, String videoId, Integer isLike) {
        if (userId == null || videoId == null) {
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

        System.out.println(prefix + "*");
        Set<Object> allFields = redisTemplate.opsForHash().keys(RedisUtil.VIDEO_LIKE);
        System.out.println("Hash 所有字段：" + allFields); // 能打印出字段 → 数据存在，问题在 scan 逻辑；打印空 → 扫错 Key/无数据
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
            System.out.println(videoId);
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
        //todo 1. 对于错误的判别，2.并发问题 3. 重复代码的封装
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

        // 结果处理（逻辑无问题，保留）
        if (result == 0) {
            throw new BusinessException(isLike == 1 ? "您已点赞该视频，无需重复操作" : "您已取消点赞该视频，无需重复操作");
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
}
