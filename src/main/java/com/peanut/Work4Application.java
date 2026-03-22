package com.peanut;

import com.peanut.utils.PathUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan({"com.peanut.dao", "com.peanut.*.dao"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
public class Work4Application {
    public static void main(String[] args) {
        PathUtil.initLogPath();
        SpringApplication.run(Work4Application.class, args);
    }

}
