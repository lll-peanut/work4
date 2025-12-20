package com.peanut.POJO;

import java.util.List;
import java.util.Map;

public class Friend {
    List<String> id;
    Map<String, List<String>> message;

    public Friend(List<String> id, Map<String, List<String>> message) {
        this.id = id;
        this.message = message;
    }

    public Friend() {}

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public Map<String, List<String>> getMessage() {
        return message;
    }

    public void setMessage(Map<String, List<String>> message) {
        this.message = message;
    }
}
