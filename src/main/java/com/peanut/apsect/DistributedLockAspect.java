package com.peanut.apsect;

import com.peanut.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

/**
 * 分布式锁的APO实现
 * 通过获取方法参数构建 "key:args1"的锁，防止重复操作
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Aspect
@Component
public class DistributedLockAspect {

    @Autowired
    private RedissonClient redissonClient;

    // SpEL 表达式解析器
    private final ExpressionParser parser = new SpelExpressionParser();

    // 获取方法参数名
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 拦截所有标注了 @DistributedLock 的方法
     */
    @Around("@annotation(com.peanut.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        // 2. 解析 SpEL 表达式，生成最终的锁 Key
        String key = annotation.prefix() + parseSpEL(annotation.key(), method, joinPoint.getArgs());

        // 3. 声明锁对象（通过 RedissonClient 获取）
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;

        try {
            // 4. 获取分布式锁（适配原注解的时间参数）
            // tryLock 参数：等待时间、自动释放时间、时间单位
            isLocked = lock.tryLock(
                    annotation.waitTime(),
                    annotation.expireTime(),
                    annotation.timeUnit()
            );

            // 未获取到锁则抛出超时异常
            if (!isLocked) {
                throw new TimeoutException("获取分布式锁超时");
            }

            // 5. 执行目标方法
            return joinPoint.proceed();
        } catch (TimeoutException e) {
            // 获取锁超时，抛出业务异常
            throw new RuntimeException(annotation.failMsg(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("线程被中断，获取锁失败", e);
        } finally {
            // 6. 最终解锁（校验锁归属，避免误删）
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 解析 SpEL 表达式
     * @param spel   表达式（如 #userId、#order.id）
     * @param method 目标方法
     * @param args   方法参数
     * @return 解析后的字符串
     */
    private String parseSpEL(String spel, Method method, Object[] args) {
        // 获取方法参数名（如 userId、order）
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames == null || parameterNames.length == 0) {
            return spel;
        }

        // 构建 SpEL 上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // 解析表达式
        return parser.parseExpression(spel).getValue(context, String.class);
    }
}