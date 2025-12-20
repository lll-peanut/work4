package com.peanut.annotation;

import com.peanut.enumPackage.LimitTypeEnum;

import java.lang.annotation.*;

/**
 * 标注在方法上，通过设置count, period, key, prefix, limitType来设置限流
 * prefix : 前缀， 一般命名格式"业务名称:limit"
 * key : 方法参数，如 "#{userId}" 控制锁的粒度
 * waitTime(等待时间), expireTime(过期时间), timeUnit(时间单位), failMsg(错误信息)
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RedisLimitAnnotation {


    /**
     * 限流时间内限流次数
     */
    int count() default 1000;

    /**
     * 限流时间，单位秒
     */
    int period() default 1;


    /**
     * key
     */
    String key() default "";

    /**
     * Key的前缀
     */
    String prefix() default "";

    /**
     * 限流的类型(接口、请求ip、用户自定义key)
     */
    LimitTypeEnum limitType() default LimitTypeEnum.INTERFACE;

}