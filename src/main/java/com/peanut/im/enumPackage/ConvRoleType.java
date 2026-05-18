package com.peanut.im.enumPackage;

/**
 * @author: peanut
 * @date: 2026/4/14
 * @version:1.0
 */
public enum ConvRoleType {

    OWNER("OWNER", "群主"),
    ADMIN("ADMIN", "管理员"),
    MEMBER("MEMBER", "普通成员");

    private final String code;
    private final String desc;

    ConvRoleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
