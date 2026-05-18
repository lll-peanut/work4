package com.peanut.utils;

import com.peanut.expection.BusinessException;
import com.peanut.expection.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUtil {

    @Value("${base.path}")
    private String basePath;

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static String postFile(MultipartFile file, String targetPath) {
        if (file == null || file.isEmpty()) {
            throw new SystemException("上传文件为空");
        }
        if (targetPath == null || targetPath.isBlank()) {
            throw new SystemException("targetPath 不能为空");
        }

        Path toTargetPath = Paths.get(targetPath).toAbsolutePath().normalize();
        Path parentDir = toTargetPath.getParent();

        try {
            // 1) 递归创建父目录
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            // 2) 写入文件（覆盖）
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, toTargetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return toTargetPath.toString();
        } catch (IOException e) {
            log.error("保存文件失败，targetPath={}, msg={}", targetPath, e.getMessage(), e);

            // 可选：只在“目标文件可能已创建/被覆盖”时再清理
            cleanFile(toTargetPath.toString());

            throw new SystemException("保存文件失败：" + e.getMessage());
        }
    }

    public static void cleanFile(String path) {
        if (path == null) {
            log.info("路径是空的");
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        } else {
            log.info("清理文件 | 路径: {}", path);
            throw new BusinessException(path + "该文件找不到");
        }
    }
}
