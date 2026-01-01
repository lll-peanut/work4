package com.peanut.utils;

import com.peanut.expection.BusinessException;
import com.peanut.expection.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static void saveFile(String filePath, String toFilePath) {
        try {
            Path sourcePath = Paths.get(filePath); // 源文件Path
            Path targetPath = Paths.get(toFilePath); // 目标文件Path（推荐拼接方式）
            Files.copy(
                    sourcePath,
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING // 覆盖已存在的文件（可选，根据需求调整）
            );
            log.info("文件上传完成{}", toFilePath);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new SystemException(e.getMessage());
        } finally {
            cleanFile(toFilePath);
        }
    }

    public static void cleanFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            log.info("文件上传完成{}", path);
        } else {
            log.info("清理失败投稿的封面文件 | 路径: {}", path);
            throw new BusinessException(path + "该文件找不到");
        }
    }

    public static String getSuffix() {
        ApplicationHome applicationHome = new ApplicationHome(FileUtil.class);
        File parentFile = applicationHome.getSource().getParentFile();
        String path = parentFile.getAbsolutePath();
        return path;
    }
}
