package com.peanut.controller;

import com.alibaba.fastjson.JSON;
import com.peanut.POJO.Base;
import com.peanut.POJO.Resp;
import com.peanut.security.LoginUser;
import com.peanut.security.service.Imp.UserDetailService;
import com.peanut.service.UserService;
import com.peanut.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token刷新
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@RestController
@RequestMapping("/token")
public class TokenRefreshController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailService userDetailsService;

    /**
     * 刷新 Access Token 接口
     * 请求头携带 Refresh Token：X-Refresh-Token: xxx
     */
    @PostMapping("/refresh")
    public String refreshAccessToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        try {
            // 1. 验证 Refresh Token 是否为空
            if (refreshToken == null || refreshToken.isEmpty()) {
                return JSON.toJSONString(Resp.fail(new Base(4001, "Refresh Token 不存在")));
            }

            // 2. 从 Refresh Token 提取用户名
            String username = jwtUtil.extractRefreshUsername(refreshToken);

            // 3. 查询用户信息
            LoginUser userDetails = userDetailsService.loadUserByUsername(username);

            // 4. 验证 Refresh Token 有效性（签名 + 未过期）
            if (!jwtUtil.validateRefreshToken(refreshToken, userDetails)) {
                return JSON.toJSONString(Resp.fail(new Base(4002, "Refresh Token 无效或已过期，请重新登录")));
            }

            // 5. 生成新的 Access Token（30分钟过期）
            String newAccessToken = jwtUtil.generateToken(userDetails);

            // 6. 响应：新 Access Token 放在响应头 + 响应体提示成功
            Resp<String> successResp = Resp.success("Access Token 刷新成功");
            // 这里用字符串返回，实际项目可封装成统一响应，同时在响应头返回新 Token
            String json = JSON.toJSONString(successResp);
            // 注意：如果用 ResponseEntity，可直接设置响应头；这里简化用字符串返回，前端从响应体或头获取均可
            // 推荐：响应头返回新 Token，响应体返回状态
            System.out.println(successResp);
            return json.replace("}", ",\"newAccessToken\":\"" + newAccessToken + "\"}"); // 临时简化，实际用 ResponseEntity 更规范

        } catch (Exception e) {
            return JSON.toJSONString(Resp.fail(new Base(5003, "Token 刷新失败：" + e.getMessage())));
        }
    }
}