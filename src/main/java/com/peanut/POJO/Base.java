package com.peanut.POJO;

public class Base {
    private long code;
    private String msg;

    public long getCode() { return code; }
    public void setCode(long value) { this.code = value; }

    public String getMsg() { return msg; }
    public void setMsg(String value) { this.msg = value; }

    public Base(long code, String msg) {
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