package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * {@link OffsetStorageConfigurer} for in-memory offset storage.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class MemoryOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * Applies the storage configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the offset-storage configuration properties
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        // Offset Store
        builder.with("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
    }

}


