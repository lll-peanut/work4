package com.peanut.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * 标注在方法上，通过设置prefix, key, waitTime, expireTime, timeUnit, failMsg来锁住方法避免并发问题
 * prefix : 前缀， 一般命名格式"业务名称:lock"
 * key : 方法参数，如 "#{userId}" 控制锁的粒度
 * waitTime(等待时间), expireTime(过期时间), timeUnit(时间单位), failMsg(错误信息)
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    String prefix();

    /**
     * 锁的 Key 主体（支持 SpEL 表达式，如：#userId、#order.id）
     * 最终 Key = prefix + key
     */
    String key();

    /**
     * 获取锁的等待时间（默认：3000 毫秒）
     */
    long waitTime() default 3000;

    /**
     * 锁的过期时间（默认：10000 毫秒）
     */
    long expireTime() default 10000;

    /**
     * 时间单位（默认：毫秒）
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    String failMsg() default "获取分布式锁失败，请稍后重试";
}