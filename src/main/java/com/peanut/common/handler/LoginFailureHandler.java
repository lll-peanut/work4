package com.peanut.common.handler;

import com.alibaba.fastjson.JSON;
import com.peanut.POJO.entity.Base;
import com.peanut.POJO.entity.Resp;
import com.peanut.POJO.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 1. 设置响应格式（JSON + UTF-8，避免中文乱码）
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("登录失败原因：" + exception.getClass().getName());
        System.out.println("登录失败信息：" + exception.getMessage());

        logger.info("帐号或密码错误，登录失败");
        // 4. 构建统一返回格式 Resp
        Resp<User> failResp = Resp.fail(new Base(400, "帐号或密码错误，登录失败\""));
        String jsonString = JSON.toJSONString(failResp);
        // 5. 序列化 JSON 并返回

        out.write(jsonString);
        out.flush();
        out.close();
    }
}
