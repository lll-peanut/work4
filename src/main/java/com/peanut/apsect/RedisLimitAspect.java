package com.peanut.apsect;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.peanut.annotation.RedisLimitAnnotation;
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
 * 细粒度的限流切面
 * 可以通过ip, 参数， 自定义来定义限流的粒度
 * 由于不知道哪个方法需要细粒度的切面，暂时没有使用到
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Aspect
@Component
public class RedisLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RedisLimitAspect.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DefaultRedisScript<Number> redisLuaScript;

    @Pointcut(value = "@annotation(com.peanut.annotation.RedisLimitAnnotation)")
    public void rateLimit() {
    }

    @Around("rateLimit()")
    public Object interceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLimitAnnotation rateLimit = method.getAnnotation(RedisLimitAnnotation.class);
        if (Objects.isNull(rateLimit)) {
            return joinPoint.proceed();
        }
        String key = getKeyByLimitType(rateLimit, signature);
        //调用lua脚本，获取返回结果，这里即为请求的次数
        Number number = redisTemplate.execute(redisLuaScript, Collections.singletonList(key), rateLimit.count(), rateLimit.period());
        if (number != null && number.intValue() != 0 && number.intValue() <= rateLimit.count()) {
            logger.info("限流时间段内访问了第：{} 次", number);
            return joinPoint.proceed();
        }
        throw new RuntimeException("访问频率过快，被限流了");
    }

    /**
     * redis key 有三种
     * 1. 自定义Key: prefix + ":" + key (key 不能为空)
     * 2. 接口key：prefix + ":" + 接口全类名
     * 3. Ip key：prefix + ":" + ip + "-" + 接口全类名
     * @param redisLimitAnnotation
     * @param signature
     * @return java.lang.String
     */
    private String getKeyByLimitType(RedisLimitAnnotation redisLimitAnnotation, MethodSignature signature) {
        String key = "";
        LimitTypeEnum limitTypeEnum = redisLimitAnnotation.limitType();
        String prefix = redisLimitAnnotation.prefix();
        if (StringUtils.isNotBlank(prefix)) {
            key = prefix + ":";
        }
        // 自定义
        if (LimitTypeEnum.CUSTOMER == limitTypeEnum) {
            String tempKey = redisLimitAnnotation.key();
            if (StringUtils.isBlank(tempKey)) {
                throw new RuntimeException("自定义类型下 key 不能为空!");
            }
            return key + tempKey;
        }
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        String classFullName = targetClass.getName() + "-" + method.getName();
        if (LimitTypeEnum.INTERFACE == limitTypeEnum) {
            return key + classFullName;
        }
        // IP
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = IPUtil.getIpAddr(request);
        return key + ipAddress + "-" + classFullName;
    }

}