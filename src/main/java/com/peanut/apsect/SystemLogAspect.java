package com.peanut.apsect;

import com.peanut.annotation.SystemLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.aspectj.lang.reflect.MethodSignature;


/**
 * 打印方法名称的切面
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Component
@Aspect
public class SystemLogAspect {

    //todo 之后可以加本地缓存id，然后日志输出id .dockerignore

    private static Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    @Pointcut("@annotation(com.peanut.annotation.SystemLog)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        SystemLog systemLog = signature.getMethod().getAnnotation(SystemLog.class);
        logger.info("操作事件: " + systemLog.operation());
    }
}
