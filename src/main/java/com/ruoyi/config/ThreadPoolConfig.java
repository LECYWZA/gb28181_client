package com.ruoyi.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;
@Data
@EnableAsync    // 开启异步
@Configuration  // 配置类
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolConfig {


    /*
        当线程池已经被关闭，或者任务数超过maximumPoolSize+workQueue时执行拒绝策略
        ThreadPoolExecutor.AbortPolicy 默认拒绝策略，丢弃任务并抛出RejectedExecutionException异常
        ThreadPoolExecutor.DiscardPolicy 直接丢弃任务，但不抛出异常
        ThreadPoolExecutor.DiscardOldestPolicy 丢弃任务队列最先加入的任务，再执行execute方法把新任务加入队列执行
        ThreadPoolExecutor.CallerRunsPolicy：由创建了线程池的线程来执行被拒绝的任务
    */
    /**
     * 核心线程数
     */
    private  Integer corePoolSize = 100;
    /**
     * 最大线程数
     */
    private  Integer maxPoolSize = 100;
    /**
     * 队列容量
     */
    private  Integer queueCapacity = Integer.MAX_VALUE;
    /**
     * 设置线程活跃时间（秒）
     */
    private  Integer keepAliveSeconds = 300;

    /**
     * 设置线程名称前缀
     */
    private  String threadNamePrefix = "Client-";
    /**
     * 设置等待关闭时间 秒
     */
    private  Integer awaitTerminationSeconds = 300;
    /**
     * 等待所有任务结束后再关闭线程池
     */
    private  Boolean waitTaskCloseThread = false;


    /*@Bean("my") // 指定注入线程对象的名字
    public TaskExecutor taskExecutor() {
        // 创建线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 设置队列容量
        executor.setQueueCapacity(queueCapacity);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 设置默认线程名称 自定义
        executor.setThreadNamePrefix(threadNamePrefix);
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(waitTaskCloseThread);
        // 设置等待关闭时间 秒
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        return executor;
    }*/

    @Bean(name = "my")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("my-pool:");
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
