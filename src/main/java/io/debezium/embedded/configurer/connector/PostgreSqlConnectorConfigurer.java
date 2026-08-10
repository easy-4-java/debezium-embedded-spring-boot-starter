package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * {@link ConnectorConfigurer} for the Debezium PostgreSQL connector.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class PostgreSqlConnectorConfigurer implements ConnectorConfigurer {
    
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // Base connection configuration
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).whenNonNull().to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getDatabaseName).whenHasText().to(value -> builder.with("database.dbname", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // Database and table filtering
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));

        // PostgreSQL specific configuration
        if (properties.getPostgreSql() != null) {
            DebeziumConnectorProperties.PostgreSql postgreSql = properties.getPostgreSql();
            
            // Plugin and replication slot configuration
            map.from(postgreSql::getPluginName).whenHasText().to(value -> builder.with("plugin.name", value));
            map.from(postgreSql::getSlotName).whenHasText().to(value -> builder.with("slot.name", value));
            map.from(postgreSql::getPublicationName).whenHasText().to(value -> builder.with("publication.name", value));
            map.from(postgreSql::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            
            // SSL configuration
            map.from(postgreSql::getSslMode).whenHasText().to(value -> builder.with("database.ssl.mode", value));
            map.from(postgreSql::getSslCert).whenHasText().to(value -> builder.with("database.ssl.cert", value));
            map.from(postgreSql::getSslKey).whenHasText().to(value -> builder.with("database.ssl.key", value));
            map.from(postgreSql::getSslRootCert).whenHasText().to(value -> builder.with("database.ssl.rootcert", value));
            map.from(postgreSql::getSslPassword).whenHasText().to(value -> builder.with("database.ssl.password", value));
            
            // Event processing configuration
            map.from(postgreSql::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
            map.from(postgreSql::getIncludeQuery).to(value -> builder.with("include.query", value));
            
            // Performance optimisation configuration
            map.from(postgreSql::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
            map.from(postgreSql::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
            map.from(postgreSql::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        }
    }
}
