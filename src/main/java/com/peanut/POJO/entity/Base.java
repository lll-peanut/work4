package com.peanut.POJO.entity;

public class Base {
    private int code;
    private String msg;

    public int getCode() { return code; }
    public void setCode(int value) { this.code = value; }

    public String getMsg() { return msg; }
    public void setMsg(String value) { this.msg = value; }

    public Base(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Base() {}

    @Override
    public String toString() {
        return "Empty{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}