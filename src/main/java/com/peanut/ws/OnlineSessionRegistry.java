package com.peanut.ws;

import jakarta.websocket.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */
public final class OnlineSessionRegistry {
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
}