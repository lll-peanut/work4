package com.peanut.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Date;

public class TokenUtil {

    private static final long EXP_TIME = 15 * 60 * 1000 * 60;
    private static final String TOKEN_SECRET = "token";

    /**
     * 签名生成
     * @return
     */
    public static String sign(String name,String userId){

        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXP_TIME);
            token = JWT.create()
                    .withIssuer("pineapple").withClaim("id",userId)
                    .withClaim("username", name)
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;

    }
    /**
     * 签名验证
     * @param token
     * @return
     */
    public static DecodedJWT verify(String token){

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("pineapple").build();
            DecodedJWT jwt = verifier.verify(token);
//            HashMap<String, String> map = new HashMap<>();
//            map.put("id", jwt.getClaim("id").asString());
//            ThreadLocalUtil.set(map);
            return jwt;
        } catch (Exception e){
            return null;
        }
    }

    public static String getId(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("pineapple").build();
        DecodedJWT jwt = verifier.verify(token);
        String id = jwt.getClaim("id").asString();
        return id;
    }

    public static String getId() {
        // 1. 获取当前 HTTP 请求（切面中必须通过 RequestContextHolder 拿到请求上下文）
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 非 Web 环境（如定时任务、内部调用），无请求头，返回 null
            System.out.println("无 HTTP 请求上下文，无法获取 Token");
            return null;
        }
        HttpServletRequest request = attributes.getRequest();

        // 2. 从请求头获取 Token（根据前后端约定的头名称调整，比如前端传的是 "token" 就用 request.getHeader("token")）
        String token = request.getHeader("Access-Token");
        if (token == null || token.trim().isEmpty()) {
            return "游客";
        }
        return getId(token);
    }
}
