package com.peanut.service;

import com.peanut.POJO.entity.Image;
import com.peanut.dao.ImageDao;
import com.peanut.utils.FileUtil;
import com.peanut.utils.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/5/12
 * @version:1.0
 */
@Service
public class ImageService {

    private final ImageDao imageDao;

    @Value("${base.path}")
    private String basePath;

    public ImageService(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    public Image insertImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("read file bytes failed", e);
        }

        // 1) 计算MD5（基于图片原始数据）
        String md5 = md5Hex(bytes);

        // 2) 去重：如果已有相同md5，直接返回已有url
        Image urlByMd5 = imageDao.findUrlByMd5(md5);
        if (urlByMd5 != null) {
            logger.info("该md5的图片已存在，直接返回url | md5: {} | url: {}", md5, urlByMd5);
            return urlByMd5;
        }

        // 3) 上传到对象存储/文件服务器，拿到url
        // 建议把md5用于文件名，避免重复上传：md5 + 原始扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = getFileExt(originalFilename); // ".png"/".jpg" 等，可能为空
        String objectKey = basePath + md5 + ext;

        String url;
        try {
            url = FileUtil.postFile(file, objectKey);
        } catch (Exception e) {
            throw new RuntimeException("upload to storage failed", e);
        }
        Image image = new Image(IdUtil.getId(), md5, url, LocalDateTime.now());

        // 4) 入库
        int i = imageDao.insertImage(image);

        return image;
    }

    private static String md5Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            return toHexLower(digest);
        } catch (Exception e) {
            throw new RuntimeException("md5 calc failed", e);
        }
    }

    private static String toHexLower(byte[] bytes) {
        char[] hex = "0123456789abcdef".toCharArray();
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = hex[v >>> 4];
            out[i * 2 + 1] = hex[v & 0x0F];
        }
        return new String(out);
    }

    private static String getFileExt(String filename) {
        if (!StringUtils.hasText(filename)) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return "";
        return filename.substring(idx).toLowerCase(); // includes dot
    }
}
