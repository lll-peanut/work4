package com.peanut.annotation;

import java.lang.annotation.*;

/**
 * 当前登录用户ID参数注解
 * 标注在Controller方法参数上，自动注入当前登录用户的ID（String类型）
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUserId {
}
