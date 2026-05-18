package com.peanut.controller;

import com.peanut.POJO.entity.Resp;
import com.peanut.POJO.entity.User;
import com.peanut.annotation.CurrentUserId;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.annotation.SystemLog;
import com.peanut.apsect.SystemLogAspect;
import com.peanut.im.pojo.dto.LoginRequest;
import com.peanut.security.LoginUser;
import com.peanut.service.UserService;
import com.peanut.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.jdbc.Null;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户控制器
 * 功能： 注册， 上传头像， 获取用户信息
 *
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */

@RestController()
@RequestMapping("/user")
@RedisLimitOnClassAnnotation
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    // 使用构造函数注入
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * 作用： 注册用户
     *
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
     *
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
     *
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

    @PostMapping("/image/search")
    public Resp<String> imageSearch(@RequestParam MultipartFile data, @CurrentUserId String id) {
        String url = userService.imageSearch(data, id);
        logger.info("{}: 以图搜图成功, 返回: {}", id, url);
        return Resp.success(url);
    }

    @PostMapping("/login")
    public Resp<User> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            Authentication authenticationRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(req.getUsername(), req.getPassword());

            Authentication authenticationResult =
                    authenticationManager.authenticate(authenticationRequest);

            // 认证成功后，principal 一般就是你 UserDetailsService 返回的 LoginUser
            LoginUser loginUser = (LoginUser) authenticationResult.getPrincipal();
            String id = loginUser.getUserId();

            // 生成 JWT（你按 JwtUtil 的方法名改一下）
            String accessToken = jwtUtil.generateToken(loginUser);
            String refreshToken = jwtUtil.generateRefreshToken(loginUser);
            response.setHeader("X-Access-Token", accessToken);
            response.setHeader("X-Refresh-Token", refreshToken);
            response.setHeader("X-Access-Expire", String.valueOf(jwtUtil.getAccessExpiration()));
            logger.info(id + "： 登录成功");
            return Resp.success(userService.getInfo(id));
        } catch (BadCredentialsException e) {
            logger.info("帐号或密码错误，登录失败");
            logger.info(e.getMessage());
            return Resp.failure(401, "用户名或密码错误");
        }
    }
}
