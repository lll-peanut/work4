package com.peanut;
import java.lang.System;

import com.peanut.utils.PathUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;

@SpringBootApplication
@MapperScan(value = "com.peanut",excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.peanut.service\\..*")
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
public class Work4Application {
    public static void main(String[] args) {
        PathUtil.initLogPath();
        SpringApplication.run(Work4Application.class, args);
    }

}
