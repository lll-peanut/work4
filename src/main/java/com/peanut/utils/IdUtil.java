package com.peanut.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * @author: peanut
 * @date: 2026/4/17
 * @version:1.0
 */
public class IdUtil {

    public static String getId() {
        return IdWorker.getIdStr();
    }
}
