package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * {@link ConnectorConfigurer} for the Debezium IBM Db2 connector.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class Db2ConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.db2.Db2Connector");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // Base connection configuration
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).whenNonNull().to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // Database and table filtering
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));
    }
}
