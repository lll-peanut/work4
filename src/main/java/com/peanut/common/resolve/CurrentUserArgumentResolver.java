package com.peanut.common.resolve;

import com.peanut.POJO.entity.User;
import com.peanut.annotation.CurrentUserId;
import com.peanut.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 获取当前用户id的resolver
 *
 * @author: peanut
 * @date: 2025/12/18
 * @version:1.0
 */
@Component // 关键：交给 Spring 管理
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver, ApplicationContextAware {


    Logger logger = LoggerFactory.getLogger(CurrentUserArgumentResolver.class);

    // 保存 Spring 上下文（用于动态获取 Bean）
    private static ApplicationContext applicationContext;

    // 无需直接 @Autowired，而是动态获取
    private UserService userService;

    // 实现接口方法，注入 ApplicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CurrentUserArgumentResolver.applicationContext = applicationContext;
    }

    // 1. 判断参数是否需要处理
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && parameter.getParameterType().equals(String.class);
    }

    // 2. 核心逻辑：解析并返回用户 ID
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 获取注解的required属性
        CurrentUserId annotation = parameter.getParameterAnnotation(CurrentUserId.class);
        boolean required = annotation.required();
        // 从 Spring Security 上下文获取登录认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 未登录处理
        if (authentication == null || authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            if (required) {
                // 必填场景：抛未登录异常
                logger.error("用户未登录");
                throw new UsernameNotFoundException("用户未登录，无法获取用户ID");
            } else {
                // 非必填场景：返回未登录用户，不抛异常
                return "未登录用户";
            }
        }
        // 从认证信息中获取登录用户名（和登录时的 username 一致）
        String loginUsername = authentication.getName();
        // 按用户名查询用户（复用 UserService 的 selectByUsername 方法，确保数据库中存在该用户）
        if (userService == null) {
            userService = applicationContext.getBean(UserService.class);
            // 打印日志验证（解决后会显示非 null）
        }
        User user = userService.selectByUserName(loginUsername);
        return user.getId();
    }
}