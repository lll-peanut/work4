package com.peanut.controller;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.peanut.dao.GroupDao;
import com.peanut.POJO.Message;
import com.peanut.config.SpringContextHolder;
import com.peanut.utils.TokenUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ServerEndpoint("/grou")
@Component
public class GroupSocketServer2 {

    private static StringRedisTemplate stringRedisTemplate;

    private GroupDao groupDao;

    @OnOpen
    public void onOpen(Session session) {
        groupDao = SpringContextHolder.getBean(GroupDao.class);
        stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);

        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
        Map<String, String> pathParameters = session.getPathParameters();
        String groupId = pathParameters.get("groupId");
        String token = requestParameterMap.get("token").get(0);
        DecodedJWT verify = TokenUtil.verify(token);
        if (verify == null) {
            closeConnection(session, "token错误");
            return;
        }
        String userId = TokenUtil.getId(token);
        if (groupDao.getId(groupId) != null && groupDao.getId(groupId) == 1) {
            session.getUserProperties().put("groupId", groupId);
            session.getUserProperties().put("userId", userId);
            String key = "group" + ":groupId:" + groupId + ":groupId";
            SetOperations<String, String> stringStringSetOperations =
                    stringRedisTemplate.opsForSet();
            Set<String> members = stringStringSetOperations.members(key);
            boolean contains = members.contains(userId);
            if (contains) {
                return;
            }
            stringStringSetOperations.add(key, userId);
        } else {
            closeConnection(session, "群号错误");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Set<Session> openSessions = session.getOpenSessions();
        Message messageObj = JSON.parseObject(message, Message.class);
        message = messageObj.getContent();
        for (Session session1 : openSessions) {
            try {
                session1.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String userId = (String) session.getUserProperties().get("userId");
        String groupId = (String) session.getUserProperties().get("groupId");
        String key = "group" + ":groupId:" + groupId + ":groupId:" + userId + ":userId";
        Long increment = stringRedisTemplate.opsForValue().increment(key);
        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
        hashOperations.put(key + ":" + increment, "message", message);
        Long time = System.currentTimeMillis();
        hashOperations.put(key + ":" + increment, "time", time.toString());
//        stringRedisTemplate.opsForZSet().incrementScore(key, userId, 1);
//        String groupZset = "group" + ":groupId:" + groupId + ":groupId";
//        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
//        Long time = System.currentTimeMillis();
//        Set<String> range = zSetOperations.range(key, 0, 0);
//        String messageNum;
//        if (!range.isEmpty() && range != null) {
//            messageNum = range.iterator().next();
//        } else {
//            messageNum = "0";
//        }
//        Integer intMessageNum = Integer.parseInt(messageNum) + 1;
//        messageNum = intMessageNum.toString();
//        zSetOperations.add(key, messageNum, time);
//        HashOperations<String, Object, Object> hashOperations = stringRedisTemplate.opsForHash();
//        hashOperations.put(key + ":" + messageNum, "message", message);
//        hashOperations.put(key + ":" + messageNum, "time", time.toString());

    }

    // 断开连接的工具方法
    private void closeConnection(Session session, String reason) {
        try {
            // 发送关闭原因（可选）
            session.getBasicRemote().sendText("连接关闭：" + reason);
            // 关闭连接，可指定关闭代码和原因
            session.close();
            System.out.println("已断开连接，原因：" + reason);
        } catch (IOException e) {
            System.err.println("关闭连接失败：" + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
