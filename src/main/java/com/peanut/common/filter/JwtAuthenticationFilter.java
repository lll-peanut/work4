package com.peanut.common.filter;

import com.peanut.security.LoginUser;
import com.peanut.security.service.Imp.UserDetailService;
import com.peanut.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt拦截器
 * 判断是否为合法用户并放行
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Lazy
    @Autowired
    private JwtUtil jwtUtil;

    @Lazy
    @Autowired
    private UserDetailService userDetailsService;


    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final String[] SKIP_PATHS = {
            "/user/login",
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String p : SKIP_PATHS) {
            if (MATCHER.match(p, uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 步骤 1：从请求头提取 Token（格式：Authorization: Bearer xxxxxx）
        String token = request.getHeader("X-Access-Token");
        String username = null;

        // 校验请求头格式：必须以 "Bearer " 开头
        if (token != null && !token.isEmpty()) {
            try {
                username = jwtUtil.extractUsername(token); // 从 Token 中解析用户名
            } catch (Exception e) {
                // Token 解析失败（如签名错误、格式错误），直接放行，后续会抛 401
                logger.error("Token 解析失败：{}", e.getMessage());
            }
        }

        // 步骤 2：Token 有效且用户未登录（SecurityContext 中无认证信息）
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 步骤 3：从数据库查询用户信息（UserDetailService 需自定义实现）
            LoginUser userDetails = userDetailsService.loadUserByUsername(username);

            // 步骤 4：验证 Token 有效性（签名正确 + 未过期）
            if (jwtUtil.validateToken(token, userDetails)) {
                // 步骤 5：将用户信息注入 SecurityContext（关键！让 Spring Security 认可登录状态）
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // 主体：用户信息
                        null, // 凭证：Token 验证场景下无需密码
                        userDetails.getAuthorities() // 用户权限/角色
                );
                // 附加请求详情（如 IP、Session ID）
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 注入上下文（后续接口可通过 SecurityContext 获取用户信息）
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 步骤 6：放行请求，继续执行后续过滤器（如权限校验、接口业务逻辑）
        filterChain.doFilter(request, response);
    }
}