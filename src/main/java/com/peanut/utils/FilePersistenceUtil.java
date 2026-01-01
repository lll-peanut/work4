package com.peanut.utils;

import com.peanut.expection.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author: peanut
 * @date: 2025/12/20
 * @version:1.0
 */
@Component
public class FilePersistenceUtil {
    // 项目专属临时目录（可配置在application.yml）
    @Value("${server.tomcat.temp-dir:${server.tomcat.basedir}/temp}")
    private String tempDir;

    private static final Logger log = LoggerFactory.getLogger(FilePersistenceUtil.class);

    /**
     * 把MultipartFile转存为本地持久化文件，返回文件路径
     */
    public String persistFile(MultipartFile multipartFile, String taskId, String fileType) throws IOException {
        // 1. 创建目录（不存在则创建）
        File dir = new File(tempDir);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) {
                log.error("文件持久化失败 | taskId: {} | 路径: {}", taskId, tempDir);
                throw new BusinessException("创建文件目录失败 | taskId: " + taskId);
            }
        }
        // 2. 生成唯一文件名（避免重复）
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = fileType + "_" + System.currentTimeMillis() + suffix;
        String filePath = dir.getAbsolutePath() + File.separator + fileName;
        // 3. 转存文件（从Tomcat临时目录复制到持久化目录）
        File targetFile = new File(filePath);
        multipartFile.transferTo(targetFile);
        // 4. 设置文件可读写，且标记为“临时文件”，后续异步任务完成后清理
        targetFile.setWritable(true);
        targetFile.setReadable(true);
        log.info("文件已持久化 | taskId: {} | 路径: {}", taskId, filePath);
        return filePath;
    }

    /**
     * 删除持久化的临时文件（异步任务完成后调用）
     */
    public static void deletePersistedFile(String filePath) {
        if (filePath == null) {
            log.error("删除持久化的文件为空");
            throw new BusinessException("路径为空");
        }
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("清理持久化临时文件 | 路径: {}", filePath);
            } else {
                log.warn("清理持久化临时文件失败 | 路径: {}", filePath);
            }
        }
    }
}
