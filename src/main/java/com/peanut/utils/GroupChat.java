package com.peanut.utils;

import jakarta.websocket.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroupChat {

    private final ConcurrentHashMap<String, Set<Session>> groupSessions = new ConcurrentHashMap<>();

    public void join(Session session) {

    }
}
