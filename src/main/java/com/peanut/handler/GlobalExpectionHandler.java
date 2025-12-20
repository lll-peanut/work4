package com.peanut.handler;

import com.peanut.POJO.Base;
import com.peanut.POJO.Resp;
import com.peanut.expection.BusinessException;
import com.peanut.expection.SystemException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

@RestControllerAdvice
public class GlobalExpectionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExpectionHandler.class);

    private static final String PROJECT_BASE_PACKAGE = "com.peanut";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Resp<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 1. 解析具体的字段校验错误
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            // 拼接：字段名 + 拒绝的值 + 错误提示
            errorMsg.append("[Field error in object '")
                    .append(fieldError.getObjectName()).append("' on field '")
                    .append(fieldError.getField()).append("': rejected value [")
                    .append(fieldError.getRejectedValue()).append("]; default message [")
                    .append(fieldError.getDefaultMessage()).append("]] ")
                    .append(System.lineSeparator());
        }
        String finalErrorMsg = errorMsg.toString().trim();

        // 2. 打印完整日志（包含请求信息+完整错误信息+堆栈）
        logger.error("{}【**参数校验异常**】: {} {}- 错误信息：{}，{}报错位置： {}",
                System.lineSeparator(),
                getRequestInfo(),
                System.lineSeparator(),
                finalErrorMsg,
                System.lineSeparator(),
                getCustomStackTrace(e));

        // 3. 返回统一响应
        return Resp.fail(new Base(400, finalErrorMsg));
    }

    @ExceptionHandler(SystemException.class)
    public Resp<Null> handleException(SystemException e) {
        logger.warn("{}【系统异常】: {} {}- 错误信息：{}，{}报错位置： {}", System.lineSeparator(), getRequestInfo(), System.lineSeparator(), e.getMessage(), System.lineSeparator(), getCustomStackTrace(e));
        return Resp.fail(new Base(500, e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public Resp<Null> handleException(BusinessException e) {
        logger.warn("{}【用户操作异常】: {} {}- 错误信息：{}，{}报错位置： {}", System.lineSeparator(), getRequestInfo(), System.lineSeparator(), e.getMessage(), System.lineSeparator(), getCustomStackTrace(e));
        return Resp.fail(new Base(400, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public Resp<Null> handleException(Exception e) {
        logger.error("{}【**异常**】: {} {}- 错误信息：{}，{}报错位置： {}", System.lineSeparator(), getRequestInfo(), System.lineSeparator(), e.getMessage(), System.lineSeparator(), getCustomStackTrace(e));
        return Resp.fail(new Base(501, e.getMessage()));
    }

    /**
     * 核心工具方法：只保留自己项目包下的堆栈信息
     */
    private String getCustomStackTrace(Throwable e) {
        if (e == null) {
            return "无异常信息";
        }

        Throwable rootCause = getRootCause(e);
        StackTraceElement[] stackTrace = rootCause.getStackTrace();

        if (stackTrace == null || stackTrace.length == 0) {
            return "无堆栈信息";
        }

        // 2. 过滤：只保留项目基础包下的堆栈
        List<StackTraceElement> customStacks = new ArrayList<>();
        for (StackTraceElement element : stackTrace) {
            // 类名以 PROJECT_BASE_PACKAGE 开头（即自己写的代码）
            if (element.getClassName().startsWith(PROJECT_BASE_PACKAGE)) {
                customStacks.add(element);
            }
        }

        // 3. 若没有匹配的堆栈（理论上不会发生），保留前3行完整堆栈（避免无信息）
        if (customStacks.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                sb.append(formatStackTraceElement(stackTrace[i])).append("\n");
            }
            return sb.toString();
        }

        // 4. 格式化过滤后的堆栈（模仿默认堆栈格式）
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : customStacks) {
            sb.append(formatStackTraceElement(element)).append("\n");
        }
        return sb.toString();
    }

    /**
     * 辅助方法：格式化单个堆栈元素（输出类似默认格式）
     */
    private String formatStackTraceElement(StackTraceElement element) {
        // 格式：at 类名.方法名(文件名:行号)
        return String.format("at %s.%s(%s:%d)",
                element.getClassName(),    // 类名（如 com.pineapplesystem.config.CustomUserDetailsService）
                element.getMethodName(),  // 方法名（如 loadUserByUsername）
                element.getFileName(),    // 文件名（如 CustomUserDetailsService.java）
                element.getLineNumber()   // 行号（如 25）
        );
    }

    private String getRequestInfo() {
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "无请求上下文";
        }
        HttpServletRequest request = attributes.getRequest();

        // 1. 基础请求信息
        String url = request.getRequestURL().toString();
        String method = request.getMethod(); // GET/POST/PUT 等
        String clientIp = request.getRemoteAddr(); // 客户端IP

        // 2. 请求参数（获取所有参数，含 form-data、query 参数）
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }

        // 3. 拼接请求信息字符串
        return String.format(
                "请求URL：%s，请求方法：%s，客户端IP：%s，请求参数：%s",
                url, method, clientIp, paramMap
        );
    }
}
