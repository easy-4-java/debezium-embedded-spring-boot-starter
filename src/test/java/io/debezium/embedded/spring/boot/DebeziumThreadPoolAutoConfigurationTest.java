package io.debezium.embedded.spring.boot;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class DebeziumThreadPoolAutoConfigurationTest {
    @Test
    void executorUsesConfiguredCapacityAndExecutesTasks() throws Exception {
        DebeziumThreadPoolProperties properties = new DebeziumThreadPoolProperties();
        properties.setCorePoolSize(1);
        properties.setMaxPoolSize(2);
        properties.setQueueCapacity(2);
        properties.setKeepAlive(Duration.ofSeconds(3));
        properties.setThreadNamePrefix("test-debezium-");
        ThreadPoolTaskExecutor executor = new DebeziumThreadPoolAutoConfiguration().debeziumEmbeddedExecutor(properties);
        executor.initialize();
        try {
            Future<String> result = executor.submit(() -> Thread.currentThread().getName());
            assertTrue(result.get().startsWith("debezium-embedded-thread-"));
            assertEquals(1, executor.getCorePoolSize());
            assertEquals(2, executor.getMaxPoolSize());
        } finally {
            executor.shutdown();
        }
    }
}
