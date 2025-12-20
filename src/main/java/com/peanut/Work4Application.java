package com.peanut;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan(value = "com.peanut",excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.peanut.service\\..*")
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
public class Work4Application {

    public static void main(String[] args) {
        SpringApplication.run(Work4Application.class, args);
    }

}
