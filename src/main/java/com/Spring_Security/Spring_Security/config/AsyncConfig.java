package com.Spring_Security.Spring_Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core thread pool
        executor.setCorePoolSize(5);

        // Max threads when load is high
        executor.setMaxPoolSize(20);

        // Queue for pending tasks
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("Async-Executor-");

        executor.initialize();
        return executor;
    }
}
