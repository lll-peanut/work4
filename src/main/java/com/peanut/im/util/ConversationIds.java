package com.peanut.im.util;

/**
 * @author: peanut
 * @date: 2026/3/7
 * @version:1.0
 */
public class ConversationIds {
    private ConversationIds() {}

    public static String of(String a, String b) {
        if (a == null || b == null) throw new IllegalArgumentException("userId is null");
        return (a.compareTo(b) <= 0) ? (a + "_" + b) : (b + "_" + a);
    }
}
