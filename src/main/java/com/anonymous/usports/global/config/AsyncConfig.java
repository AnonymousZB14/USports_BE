package com.anonymous.usports.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 기본적으로 실행을 유지하는 스레드 수
        executor.setMaxPoolSize(10); // 동시에 실행할 수 있는 최대 스레드 수
        executor.setQueueCapacity(100); // 작업 큐의 용량
        executor.setThreadNamePrefix("AsyncExecutor-"); //생성되는 스레드의 이름 접수다
        return executor;
    }
}
