package com.peanut.controller;

import com.peanut.POJO.entity.Image;
import com.peanut.POJO.entity.Resp;
import com.peanut.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: peanut
 * @date: 2026/5/13
 * @version:1.0
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    ImageService imageService;

    private final static Logger logger = LoggerFactory.getLogger(ImageController.class);

    @PostMapping("/upload")
    public Resp<Image> postImage(@RequestParam("file") MultipartFile file) {
        Image s = imageService.insertImage(file);
        logger.info("图片上传成功，文件名：{}", file.getOriginalFilename());
        return Resp.success(s);
    }
}
