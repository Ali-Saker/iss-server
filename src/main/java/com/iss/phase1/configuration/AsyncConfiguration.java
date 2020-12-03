package com.iss.phase1.configuration;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {
    private static final Logger logger = Logger.getLogger(AsyncConfiguration.class.getName());

    public AsyncConfiguration() {
    }

    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.initialize();
        return executor;
    }

    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            logger.log(Level.SEVERE, ex.getMessage() + " " + method.getName(), ex);
        };
    }
}
