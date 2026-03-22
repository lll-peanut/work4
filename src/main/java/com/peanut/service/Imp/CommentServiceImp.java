package com.peanut.service.Imp;

import com.peanut.dao.CommentDao;
import com.peanut.POJO.Comment;
import com.peanut.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentServiceImp implements CommentService {

    @Autowired
    CommentDao commentDao;

    public List<Comment> getCommentList(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        int batchSize = 500; // 每批500个ID，MySQL推荐阈值
        List<Comment> allComments = new ArrayList<>();
        int size = ids.size();
        for (int i = 0; i < size; i += batchSize) {
            // 截取当前批次ID：[i, min(i+batchSize, 总长度))
            List<String> batchIds = ids.subList(i, Math.min(i + batchSize, size));
            // MP 内置的批量查询（selectBatchIds）
            List<Comment> batchVideos = commentDao.selectBatchIds(batchIds);
            allComments.addAll(batchVideos);
        }
        // 4. 保证返回顺序与传入ids一致（调用方体验最优）
        Map<String, Comment> videoMap = allComments.stream()
                .collect(Collectors.toMap(
                        Comment::getId,          // key=视频ID
                        comment -> comment,        // value=视频对象
                        (v1, v2) -> v1         // 重复ID保留第一个（理论上不会出现）
                ));
        // 按原始传入顺序组装，过滤掉不存在的视频（可选保留null）
        List<Comment> orderedComments = ids.stream()
                .map(videoMap::get)
                .filter(Objects::nonNull) // 过滤数据库中不存在的ID
                .collect(Collectors.toList());
        return orderedComments;
    }
}
