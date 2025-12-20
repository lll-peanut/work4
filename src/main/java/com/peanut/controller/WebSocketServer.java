package com.peanut.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.peanut.POJO.Message;
import com.peanut.config.SpringContextHolder;
import com.peanut.service.UserService;
import com.peanut.utils.TokenUtil;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket")
@Component
public class WebSocketServer {
    @Autowired
    private UserService userService;

    private StringRedisTemplate stringRedisTemplate;

    static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String token = session.getQueryString().split("=")[1];
        String id = TokenUtil.getId(token);
        if (id != null) {
            sessions.put(id, session);
            System.out.println(id);
        }
        stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        session.getUserProperties().put("userId", id);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage");
        Message msgObj = null;
        try {
            msgObj = JSON.parseObject(message, Message.class);
        } catch (JSONException e) {
            e.printStackTrace(); // 输出：JSON parse error: Expected double quote at...
            return;
        }
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            if (entry.getKey().equals(msgObj.getUserId())) {
                System.out.println(msgObj.getUserId());
                entry.getValue().getAsyncRemote().sendText(msgObj.getContent());
            }
        }
        try {
            session.getBasicRemote().sendText(message);
            ZSetOperations<String, String> stringStringZSetOperations =
                    stringRedisTemplate.opsForZSet();
            String userId = session.getUserProperties().get("userId").toString();
            String key = "user:userId:" + userId + ":userId:" + msgObj.getUserId() + ":toUserId";
            String content = msgObj.getContent();
            long time = System.currentTimeMillis();
            ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
            Set<String> strings = stringStringZSetOperations.reverseRange(key, 0, 0);
            String zsetId;
            if (strings != null && !strings.isEmpty()) {
                zsetId = strings.iterator().next();
            } else {
                zsetId = "0";
            }
            Integer i = Integer.parseInt(zsetId) + 1;
            zsetId = i.toString();
            stringStringZSetOperations.add(key, zsetId, time);
            key = key + ":" + zsetId;
            stringRedisTemplate.opsForHash().put(key, "message", content);
            stringRedisTemplate.opsForHash().put(key, "isRead", "0");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose");
        // 从sessions中移除断开连接的Session（需要反向根据Session找userId）
        String removeId = null;
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                removeId = entry.getKey();
                break;
            }
        }
        if (removeId != null) {
            sessions.remove(removeId);
        }
    }
}
