package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * Memory 型 Offset 存储配置。
 */
public class MemoryOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * 应用存储配置。
     *
     * @param builder 配置构建器
     * @param properties 存储配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        // Offset Store
        builder.with("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
    }

}


