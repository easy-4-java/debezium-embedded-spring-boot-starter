package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;

/**
 * Strategy interface for writing database-connector specific settings into a
 * Debezium {@link Configuration.Builder}.
 * <p>Each supported {@link ConnectorType} ships with a dedicated implementation
 * that knows how to translate {@link DebeziumConnectorProperties} into the
 * connector's own configuration keys.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface ConnectorConfigurer {
    /**
     * Applies the connector specific configuration to the supplied builder.
     *
     * @param builder    the Debezium configuration builder to mutate
     * @param properties the connector configuration properties
     */
    void apply(Configuration.Builder builder, DebeziumConnectorProperties properties);
}


