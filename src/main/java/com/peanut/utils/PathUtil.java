package com.peanut.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

/**
 * 配置系统环境路径 base.path
 * @author: peanut
 * @date: 2026/1/1
 * @version:1.0
 */
public class PathUtil {

    public static void initLogPath() {

        try {
            ApplicationHome applicationHome = new ApplicationHome(PathUtil.class);
            File sourceFile = applicationHome.getSource();
            String jarPath;

            if (sourceFile != null && sourceFile.getParentFile() != null) {
                jarPath = sourceFile.getParentFile().getAbsolutePath();
            } else {
                jarPath = System.getProperty("user.dir");
            }
            // 设置系统属性
            System.setProperty("base.path", jarPath);
            System.out.println("日志基础路径已设置：" + jarPath);
        } catch (Exception e) {
            System.err.println("初始化路径失败" + e);
            throw new RuntimeException(e);
        }
    }
}
