package com.peanut.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配置异步线程池
 * @author: peanut
 * @date: 2025/12/19
 * @version:1.0
 */
@Configuration
public class AsyncThreadPoolConfig {
    @Value("${thread.pool.core-size:8}")
    private int coreSize;
    @Value("${thread.pool.max-size:16}")
    private int maxSize;
    @Value("${thread.pool.keep-alive-seconds:60}")
    private int keepAliveSeconds;
    @Value("${thread.pool.queue-capacity:100}")
    private int queueCapacity;

    private static final Logger log = LoggerFactory.getLogger(AsyncThreadPoolConfig.class);

    /**
     * 视频上传专用线程池
     * @return 自定义线程池
     */
    @Bean(name = "videoUploadExecutor")
    public Executor videoUploadExecutor() {
        // 修正核心/最大线程数 cpu的2倍和4倍
        int cpuCore = Runtime.getRuntime().availableProcessors();
        coreSize = cpuCore * 2;
        maxSize = cpuCore * 4;
        log.info("初始化视频上传线程池：核心线程数={}, 最大线程数={}, 队列容量={}", coreSize, maxSize, queueCapacity);

        // 参数分别为： 核心线程数， 最大线程数， 非核心线程空闲存活时间， 时间单位， 任务队列，自定义线程工厂， 自定义拒绝策略
        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),    //有界队列，避免溢出
                new ThreadFactory() {
                    // 原子计数器：多线程环境下安全生成唯一的线程编号（避免线程名重复）
                    private final AtomicInteger threadNum = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        // 自定义线程名，便于问题排查
                        thread.setName("video-upload-thread-" + threadNum.getAndIncrement());
                        // 非守护线程，保证上传任务完成
                        thread.setDaemon(false);
                        // 设置线程优先级（IO密集型设为NORM_PRIORITY即可）
                        thread.setPriority(Thread.NORM_PRIORITY);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                        log.warn("视频上传线程池任务已满，由调用线程执行：{}", Thread.currentThread().getName());
                        super.rejectedExecution(r, e);
                    }
                }
        );
    }
}
