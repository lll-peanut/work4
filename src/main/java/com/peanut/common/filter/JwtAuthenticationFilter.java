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
 *
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

        // 0) 如果已经有认证信息，直接放行（避免重复解析 JWT）
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1) 提取 token（兼容 X-Access-Token / Authorization: Bearer）
        String token = resolveToken(request);
        if (token == null) {
            // 是否需要登录由 Spring Security 的授权规则决定
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2) 先验签 + 校验过期（不查库）
            if (!jwtUtil.validateAccessTokenSignatureAndExpiry(token)) {
                // token 不合法：不注入认证，交给后续的 Security 决定是否 401
                logger.debug("JWT 无效（验签/过期失败），uri={}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // 3) token 可信后再提取 username
            String username = jwtUtil.extractUsername(token);
            if (username == null || username.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            // 4) 查库拿 userDetails（用于权限/封禁/用户不存在等判定）
            LoginUser userDetails = userDetailsService.loadUserByUsername(username);

            // 5) （可选）进一步校验：比如 user 是否禁用、tokenVersion 等
            // 如果你想支持“踢下线/强制失效”，建议在这里做额外校验

            // 6) 注入 SecurityContext
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, // 主体：用户信息
                    null, // 凭证：Token 验证场景下无需密码
                    userDetails.getAuthorities() // 用户权限/角色
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            // 不要 error 级别刷屏（无效 token 可能很多）
            logger.debug("JWT 处理异常，uri={}, msg={}", request.getRequestURI(), e.getMessage());
            // 不注入认证，继续走，让后续决策
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String t = request.getHeader("X-Access-Token");
        if (t != null) {
            t = t.trim();
            if (!t.isBlank()) return t;
        }

        String auth = request.getHeader("Authorization");
        if (auth != null) {
            auth = auth.trim();
            String BEARER_PREFIX = "Bearer ";
            if (auth.startsWith(BEARER_PREFIX)) {
                String token = auth.substring(BEARER_PREFIX.length()).trim();
                return token.isBlank() ? null : token;
            }
            // 兼容部分客户端直接 Authorization: <token>
            return auth.isBlank() ? null : auth;
        }
        return null;
    }
}