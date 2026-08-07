package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * File 型 Offset 存储配置。
 */
public class FileOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * 应用存储配置。
     *
     * @param builder 配置构建器
     * @param properties 存储配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.File file = properties.getFile();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        // Offset Store
        builder.with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        map.from(file::getFileName).whenHasText().to(value -> builder.with("offset.storage.file.filename", value));
        map.from(file::getFlushIntervalMs).to(value -> builder.with("offset.flush.interval.ms", value));
        map.from(file::getFlushTimeoutMs).to(value -> builder.with("offset.flush.timeout.ms", value));

    }
}


