package com.peanut.ws;


import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: peanut
 * @date: 2026/3/22
 * @version:1.0
 */
public final class GroupSessionRegistry {

    private static final Logger logger = LoggerFactory.getLogger(GroupSessionRegistry.class);

    // 群号 -> 该群所有在线客户端session（支持多端多次加入）
    private static final ConcurrentHashMap<String, Set<Session>> GROUP_ONLINE = new ConcurrentHashMap<>();

    private GroupSessionRegistry() {
    }

    /**
     * 加入群在线会话
     */
    public static void add(String groupId, Session session) {
        GROUP_ONLINE.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    /**
     * 移除群在线会话
     */
    public static void remove(String groupId, Session session) {
        if (groupId == null) return;
        Set<Session> set = GROUP_ONLINE.get(groupId);
        if (set == null) return;
        set.remove(session);
        if (set.isEmpty()) GROUP_ONLINE.remove(groupId);
    }

    /**
     * 获取某群全部在线session（只读快照）
     */
    public static Set<Session> get(String groupId) {
        Set<Session> set = GROUP_ONLINE.get(groupId);
        return set == null ? Set.of() : Set.copyOf(set);
    }

    /**
     * 向群内所有在线成员推送消息
     */
    public static void sendToGroup(String groupId, String msg) {
        Set<Session> sessions = get(groupId);
        for (Session sess : sessions) {
            if (sess.isOpen()) {
                try {
                    sess.getAsyncRemote().sendText(msg);
                } catch (Exception e) {
                    logger.warn("WebSocket群消息推送失败，groupId={}, sessionId={}, 原因:{}", groupId, sess.getId(), e.getMessage(), e);
                    remove(groupId, sess);
                }
            } else {
                remove(groupId, sess);
            }
        }
    }
}