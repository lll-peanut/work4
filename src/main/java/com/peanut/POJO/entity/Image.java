package com.peanut.POJO.entity;

import java.time.LocalDateTime;

/**
 * @author: peanut
 * @date: 2026/5/12
 * @version:1.0
 */
public class Image {
    private String id;
    private String md5;
    private String url;
    private LocalDateTime createdAt;

    public Image() {
    }

    public Image(String id, String md5, String url, LocalDateTime createdAt) {
        this.id = id;
        this.md5 = md5;
        this.url = url;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
