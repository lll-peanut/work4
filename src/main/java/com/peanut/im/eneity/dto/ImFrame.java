package com.peanut.im.eneity.dto;

/**
 * @author: peanut
 * @date: 2026/3/14
 * @version:1.0
 */
public class ImFrame<T> {
    private String type;
    private T data;

    public ImFrame() {}

    public ImFrame(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}