package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * Strategy interface for writing schema-history backend settings into a
 * Debezium {@link Configuration.Builder}.
 * <p>Each {@link SchemaHistoryType} ships with a dedicated implementation.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface SchemaHistoryConfigurer {

    /**
     * Applies the schema-history configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the schema-history configuration properties
     */
    void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties);
}
