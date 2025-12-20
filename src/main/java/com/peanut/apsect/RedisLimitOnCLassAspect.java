package com.peanut.apsect;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.peanut.annotation.RedisLimitOnClassAnnotation;
import com.peanut.enumPackage.LimitTypeEnum;
import com.peanut.utils.IPUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Objects;

/**
 * 粗粒度的限流切面
 * 可以通过ip, 参数， 自定义来定义
 *
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Aspect
@Component
public class RedisLimitOnCLassAspect {

    private static final Logger logger = LoggerFactory.getLogger(RedisLimitOnCLassAspect.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DefaultRedisScript<Number> redisLuaScript;

    @Pointcut(value = "@within(com.peanut.annotation.RedisLimitOnClassAnnotation)")
    public void rateLimitOnClass() {
    }

    @Around("rateLimitOnClass()")
    public Object interceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        RedisLimitOnClassAnnotation classLimitAnno = targetClass.getAnnotation(RedisLimitOnClassAnnotation.class);
        if (Objects.isNull(classLimitAnno)) {
            return joinPoint.proceed();
        }
        String key = classLimitAnno.key();
        //调用lua脚本，获取返回结果，这里即为请求的次数
        Number number = redisTemplate.execute(redisLuaScript, Collections.singletonList(key), classLimitAnno.count(), classLimitAnno.period());
        if (number != null && number.intValue() != 0 && number.intValue() <= classLimitAnno.count()) {
            logger.info("限流时间段内访问了第：{} 次", number);
            return joinPoint.proceed();
        }
        throw new RuntimeException("访问频率过快，被限流了");
    }

}