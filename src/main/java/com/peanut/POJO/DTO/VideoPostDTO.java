package com.peanut.POJO.DTO;

public class VideoPostDTO {
    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VideoPostDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public VideoPostDTO() {}

    @Override
    public String toString() {
        return "VideoPostDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
