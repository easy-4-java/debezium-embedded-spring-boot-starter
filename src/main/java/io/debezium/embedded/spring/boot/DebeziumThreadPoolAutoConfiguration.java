package io.debezium.embedded.spring.boot;

import io.debezium.embedded.handler.DebeziumThreadUncaughtExceptionHandler;
import io.debezium.engine.DebeziumEngine;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Auto-configuration for the dedicated thread pool used to run the Debezium
 * embedded engine and dispatch its change events.
 * <p>Exposes a {@link ThreadPoolTaskExecutor} bean named
 * {@code debeziumEmbeddedExecutor}, configured from
 * {@link DebeziumThreadPoolProperties} and using a
 * {@link DebeziumThreadUncaughtExceptionHandler}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(DebeziumEngine.class)
@EnableConfigurationProperties(DebeziumThreadPoolProperties.class)
/**
 * <p>Auto-configuration for DebeziumThreadPoolAutoConfiguration.</p>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class DebeziumThreadPoolAutoConfiguration {

    /**
     * Builds the {@code debeziumEmbeddedExecutor} task executor used by the
     * embedded engine.
     *
     * @param poolProperties the bound {@code debezium.thread-pool.*} properties
     * @return a configured {@link ThreadPoolTaskExecutor}; shut down on context close
     */
    @Bean(destroyMethod = "shutdown", name = "debeziumEmbeddedExecutor")
    /**
     * <p>Debezium embedded executor.</p>
     * @param poolProperties
     * @return the result
     */
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
         * Rejected-execution policies:
         * CallerRunsPolicy() - run the task on the caller thread (e.g. main thread)
         * AbortPolicy()       - throw a RejectedExecutionException
         * DiscardPolicy()     - silently discard the new task
         * DiscardOldestPolicy() - drop the oldest queued task and retry
         */
        executor.setRejectedExecutionHandler(poolProperties.getRejectedPolicy().getRejectedExecutionHandler());
        return executor;
    }

}
