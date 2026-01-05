package com.peanut.ws;

import com.peanut.POJO.User;
import com.peanut.config.SpringContextHolder;
import com.peanut.service.UserService;
import com.peanut.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;


import java.util.List;
import java.util.Map;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */

public class WsHandshakeConfigurator extends ServerEndpointConfig.Configurator {

    private static final String HEADER_X_ACCESS_TOKEN = "X-Access-Token";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_KEY = "userId";
    private static final String USER_NAME_KEY = "userName";
    private static final String TOKEN_KEY = "token";

    @Override public void modifyHandshake(ServerEndpointConfig sec,
                                          HandshakeRequest request,
                                          HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);

        Map<String, List<String>> headers = request.getHeaders();
        String token = resolveToken(headers);
        JwtUtil jwtUtil = SpringContextHolder.getBean(JwtUtil.class);
        String identity = jwtUtil.extractUserId(token);
        if (identity != null && !identity.isBlank()) {
            sec.getUserProperties().put(USER_ID_KEY, identity);
        }
    }

    private String resolveToken(Map<String, List<String>> headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }

        // 1) X-Access-Token: <token>
        List<String> accessTokens = headers.get(HEADER_X_ACCESS_TOKEN);
        if (accessTokens != null && !accessTokens.isEmpty()) {
            String t = accessTokens.get(0);
            if (t != null) {
                t = t.trim();
                if (!t.isBlank()) {
                    return t;
                }
            }
        }

        // 2) Authorization: Bearer <token>
        List<String> auths = headers.get(HEADER_AUTHORIZATION);
        if (auths != null && !auths.isEmpty()) {
            String v = auths.get(0);
            if (v != null) {
                v = v.trim();
                if (v.startsWith(BEARER_PREFIX)) {
                    String t = v.substring(BEARER_PREFIX.length()).trim();
                    return t.isBlank() ? null : t;
                }
                // 兼容某些客户端直接把 token 放在 Authorization 里
                if (!v.isBlank()) {
                    return v;
                }
            }
        }

        return null;
    }


}