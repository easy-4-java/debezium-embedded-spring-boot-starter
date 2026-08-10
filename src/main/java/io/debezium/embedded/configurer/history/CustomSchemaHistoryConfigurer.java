package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * {@link SchemaHistoryConfigurer} for user-provided custom schema-history backends.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class CustomSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * Applies the configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the configuration properties
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.Custom custom = properties.getCustom();
        
        if (custom.getHistoryClass() != null) {
            builder.with("schema.history.internal", custom.getHistoryClass());
            
            // Forward custom raw properties
            if (custom.getProps() != null) {
                custom.getProps().forEach((key, value) -> {
                    if (key.startsWith("schema.history.internal.")) {
                        builder.with(key, value);
                    } else {
                        builder.with("schema.history.internal." + key, value);
                    }
                });
            }
        }
    }
}
