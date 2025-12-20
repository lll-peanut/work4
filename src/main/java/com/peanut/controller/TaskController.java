package com.peanut.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.peanut.POJO.AsyncTaskResult;
import com.peanut.POJO.Base;
import com.peanut.POJO.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查询结果控制器
 * @author: peanut
 * @date: 2025/12/20
 * @version:1.0
 */
@RestController
public class TaskController {

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/task/status/{taskId}")
    public Resp getTaskStatus(@PathVariable String taskId) {
        String taskJson = (String) redisTemplate.opsForValue().get("video:task:" + taskId);
        if (taskJson == null) {
            return Resp.fail(new Base(404, "任务ID不存在或已过期"));
        }
        AsyncTaskResult<String> taskResult = JSON.parseObject(taskJson, new TypeReference<AsyncTaskResult<String>>() {});
        if (taskResult.isSuccess()) {
            return Resp.success(taskResult);
        } else {
            return Resp.fail(new Base(400, taskResult.getErrorMsg()));
        }
    }
}
