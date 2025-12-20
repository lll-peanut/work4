package com.peanut.POJO;

public class Message {
    private String userId;
    private boolean systemMessage;
    private String content;

    public Message(String userId, boolean systemMessage, String content) {
        this.userId = userId;
        this.systemMessage = systemMessage;
        this.content = content;
    }

    public Message() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(boolean systemMessage) {
        this.systemMessage = systemMessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSystemMessage() {
        return systemMessage;
    }
}
