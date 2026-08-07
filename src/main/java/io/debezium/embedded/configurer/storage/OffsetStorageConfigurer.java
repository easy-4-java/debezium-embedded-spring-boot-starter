package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * 抽象的 Offset 存储配置器，将 storage 相关配置写入 Debezium Configuration.Builder。
 */
public interface OffsetStorageConfigurer {
    /**
     * 应用存储配置。
     *
     * @param builder 配置构建器
     * @param properties 存储配置属性
     */
    void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties);
}


