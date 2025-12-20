package com.peanut.annotation;

import com.peanut.enumPackage.LimitTypeEnum;

import java.lang.annotation.*;


/**
 * 粒度大， 对于一个Controller里面的方法广泛限流
 * 标注在类上，通过设置count, key来设置限流
 * key : 类的名称，如 "video"
 * waitTime(等待时间), expireTime(过期时间), timeUnit(时间单位), failMsg(错误信息)
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Documented
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RedisLimitOnClassAnnotation {



    /**
     * 限流时间内限流次数
     */
    int count() default 1000;

    /**
     * 限流时间，单位秒
     */
    int period() default 5;


    /**
     * key
     */
    String key() default "classLimit";

}
