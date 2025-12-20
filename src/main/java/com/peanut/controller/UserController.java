package com.peanut.controller;

import com.peanut.POJO.*;
import com.peanut.annotation.CurrentUserId;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.annotation.SystemLog;
import com.peanut.apsect.SystemLogAspect;
import com.peanut.service.UserService;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器
 * 功能： 注册， 上传头像， 获取用户信息
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@RestController()
@RequestMapping("/user")
@RedisLimitOnClassAnnotation
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    @Autowired
    private UserService userService;
    /**
     * 作用： 注册用户
     * @param username
     * @param password
     * @return
     */
    @SystemLog(operation = "用户注册")
    @PostMapping("/register")
    public Resp<Null> register(@RequestParam("username") String username, @RequestParam("password") String password) {
        userService.register(username, password);
        logger.info(username + " 注册成功");
        return Resp.success(null);
    }

    /**
     * 作用： 通过id寻找用户
     * @param id
     * @return
     */
    @SystemLog(operation = "查询用户信息")
    @GetMapping("/info")
    public Resp<User> info(@RequestParam("user_id") String id) {
        User user = userService.getInfo(id);
        logger.info("查询用户:" + id);
        return Resp.success(user);
    }

    /**
     * 上传用户头像
     * @param data
     * @param id
     * @return
     */
    @PutMapping("/avatar/upload")
    public Resp<User> avator(@RequestParam MultipartFile data, @CurrentUserId String id) {
        User user = userService.getInfo(id);
        String url = userService.uploadAvatar(data, user);
        logger.info(id + ": 上传头像成功,头像地址: " + url);
        return Resp.success(user);
    }

}
