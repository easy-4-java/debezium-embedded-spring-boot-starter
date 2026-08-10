package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * {@link OffsetStorageConfigurer} for file based offset storage.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class FileOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * Applies the storage configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the offset-storage configuration properties
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


