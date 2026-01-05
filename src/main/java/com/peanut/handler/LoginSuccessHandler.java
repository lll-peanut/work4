package com.peanut.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peanut.POJO.Resp;
import com.peanut.POJO.User;
import com.peanut.security.LoginUser;
import com.peanut.service.UserService;
import com.peanut.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义登录成功处理器：返回 Resp 格式的 JSON
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. 设置响应格式（JSON + UTF-8，避免中文乱码）
        response.setContentType("application/json;charset=UTF-8");

        try(PrintWriter out = response.getWriter()) {

            // 2. 从 Authentication 中获取登录成功的用户信息（UserDetails）
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            String username = loginUser.getUsername(); // 登录用户名
            String accessToken = jwtUtil.generateToken(loginUser);
            String refreshToken = jwtUtil.generateRefreshToken(loginUser); // 7天过期

            // 2. 关键：双 Token 放在响应头（不破坏响应体）
            response.setHeader("X-Access-Token", accessToken); // Access Token
            response.setHeader("X-Refresh-Token", refreshToken); // Refresh Token
            response.setHeader("X-Access-Expire", String.valueOf(jwtUtil.getAccessExpiration())); // 过期时间（毫秒），前端用于计时刷新

            // 3. （可选）查询用户完整信息（如 id、昵称等，按需返回）
            User user = userService.selectByUserName(username);
            logger.info(user.getId() + "： 登录成功");
            // 4. 构建统一返回格式 Resp
            Resp<User> successResp = Resp.success(user);
            String jsonString = JSON.toJSONString(successResp, SerializerFeature.WriteMapNullValue);
            out.write(jsonString);
            out.flush();
        }
    }
}