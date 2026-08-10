package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * {@link SchemaHistoryConfigurer} for in-memory schema history.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class MemorySchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * Applies the in-memory history configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the configuration properties
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        // Internal schema history store
        builder.with("schema.history.internal", "io.debezium.relational.history.MemorySchemaHistory");
    }

}
