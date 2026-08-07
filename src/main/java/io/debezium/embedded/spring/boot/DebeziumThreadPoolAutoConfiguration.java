package io.debezium.embedded.spring.boot;

import io.debezium.embedded.handler.DebeziumThreadUncaughtExceptionHandler;
import io.debezium.engine.DebeziumEngine;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConditionalOnClass(DebeziumEngine.class)
@EnableConfigurationProperties(DebeziumThreadPoolProperties.class)
public class DebeziumThreadPoolAutoConfiguration {

    @Bean(destroyMethod = "shutdown", name = "debeziumEmbeddedExecutor")
    public ThreadPoolTaskExecutor debeziumEmbeddedExecutor(DebeziumThreadPoolProperties poolProperties) {
        BasicThreadFactory factory = BasicThreadFactory.builder()
                .namingPattern("debezium-embedded-thread-%d")
                .uncaughtExceptionHandler(new DebeziumThreadUncaughtExceptionHandler()).build();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadFactory(factory);
        executor.setCorePoolSize(poolProperties.getCorePoolSize());
        executor.setMaxPoolSize(poolProperties.getMaxPoolSize());
        executor.setQueueCapacity(poolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(Long.valueOf(poolProperties.getKeepAlive().getSeconds()).intValue());
        executor.setAllowCoreThreadTimeOut(poolProperties.isAllowCoreThreadTimeOut());
        executor.setAwaitTerminationSeconds(poolProperties.getAwaitTerminationSeconds());
        executor.setWaitForTasksToCompleteOnShutdown(poolProperties.isWaitForTasksToCompleteOnShutdown());
        executor.setThreadNamePrefix(poolProperties.getThreadNamePrefix());
        /*
         * 拒绝处理策略
         * CallerRunsPolicy()：交由调用方线程运行，比如 main 线程。
         * AbortPolicy()：直接抛出异常。
         * DiscardPolicy()：直接丢弃。
         * DiscardOldestPolicy()：丢弃队列中最老的任务。
         */
        executor.setRejectedExecutionHandler(poolProperties.getRejectedPolicy().getRejectedExecutionHandler());
        return executor;
    }

}
