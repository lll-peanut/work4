package com.peanut.enumPackage;

public enum LimitTypeEnum {

    // 默认限流策略，针对某一个接口进行限流
    INTERFACE,

    // 根据IP地址进行限流
    IP,

    // 自定义的Key
    CUSTOMER;


}
