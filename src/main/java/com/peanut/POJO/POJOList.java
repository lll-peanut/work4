package com.peanut.POJO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class POJOList<E> {
    private List<E> items;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;

    public POJOList() {}

    public POJOList(List<E> items, Long total) {
        this.items = items;
        this.total = total;
    }

    public List<E> getItems() {
        return items;
    }

    public void setItems(List<E> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
