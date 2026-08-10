package io.debezium.embedded.configurer.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * Strategy interface for writing offset-storage backend settings into a
 * Debezium {@link Configuration.Builder}.
 * <p>Each {@link OffsetStorageType} ships with a dedicated implementation.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public interface OffsetStorageConfigurer {
    /**
     * Applies the offset-storage configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the offset-storage configuration properties
     */
    void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties);
}


