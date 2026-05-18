package com.peanut.POJO.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.peanut.constant.Code;

public class Resp<E> {
    private Base base;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private E data;

    public Resp() {}

    public Resp(Base base, E data) {
        this.base = base;
        this.data = data;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public static<E> Resp<E> success(E body) {
        return new Resp<E>(new Base(Code.SUCCESS, "success"), body);
    }

    public static<E> Resp<E> fail(Base base) {
        return new Resp(base, null);
    }

    public static<E> Resp<E> failure(int code, String msg) {
        return new Resp(new Base(code, msg), null);
    }

    public static<E> Resp<E> common(Base base) {
        return new Resp(base, null);
    }
}
