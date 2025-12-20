package com.peanut.annotation;


import java.lang.annotation.*;

/**
 * 定义在方法上，当使用了这个方法，就会打印日志
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {
    String operation() default "";
}
