package com.peanut.ws;

import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */
public final class OnlineSessionRegistry {

    private static final Logger logger = LoggerFactory.getLogger(OnlineSessionRegistry.class);
    private OnlineSessionRegistry() {}

    private static final ConcurrentHashMap<String, Set<Session>> ONLINE = new ConcurrentHashMap<>();

    public static void add(String userId, Session session) {
        ONLINE.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public static void remove(String userId, Session session) {
        if (userId == null) return;
        Set<Session> set = ONLINE.get(userId);
        if (set == null) return;
        set.remove(session);
        if (set.isEmpty()) ONLINE.remove(userId);
    }

    public static Set<Session> get(String userId) {
        Set<Session> set = ONLINE.get(userId);
        return set == null ? Set.of() : Set.copyOf(set);
    }

    public static boolean isOnline(String userId) {
        Set<Session> set = ONLINE.get(userId);
        if (set != null && !set.isEmpty()) return false;
        return set.stream().anyMatch(session -> session.isOpen());
    }

    public static void sendToUser(String userId, String msg) {
        Set<Session> sessions = OnlineSessionRegistry.get(userId);
        if (sessions != null) {
            for (Session sess : sessions) {
                if (sess.isOpen()) {
                    try {
                        sess.getAsyncRemote().sendText(msg);
                    } catch (Exception e) {
                        // 可考虑移除异常 session 防资源泄漏
                        logger.warn("WebSocket 消息推送失败，userId={}, sessionId={}, 原因:{}", userId, sess.getId(), e.getMessage(), e);
                        OnlineSessionRegistry.remove(userId, sess);
                    }
                } else {
                    OnlineSessionRegistry.remove(userId, sess);
                }
            }
        }
    }
}